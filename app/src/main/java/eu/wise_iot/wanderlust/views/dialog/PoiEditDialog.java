package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.io.File;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.AddPoiCommand;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.controllers.OfflineQueueController;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.AddressPoint;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.views.MapFragment;
import eu.wise_iot.wanderlust.views.ProfileFragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * PoiEditDialog:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class PoiEditDialog extends DialogFragment {
    private static final String TAG = "PoiEditDialog";
    private FragmentHandler poiHandler;
    private Context context;
    private Poi poi;
    private MapController mapController;
    private GeoPoint lastKnownLocation;
    private EditText titleEditText;
    private TextInputLayout titleTextLayout;
    private EditText descriptionEditText;
    private Spinner typeSpinner;
    private Spinner modeSpinner;
    private ImageButton buttonSave;
    private ImageButton buttonCancel;
    private PoiController controller;
    private ImageController imageController;
    private boolean isNewPoi;
    private boolean publish;
    private FragmentHandler poiPhotoUploadHandler;
    private OfflineQueueController offlineQueueController;

    /**
     * Create EditPoit dialog, which is used for CREATING a Poi
     *
     * @param imageFileName
     * @param lastKnownLocation
     */
    public static PoiEditDialog newInstance(String imageFileName, GeoPoint lastKnownLocation) {
        PoiEditDialog fragment = new PoiEditDialog();
        Bundle args = new Bundle();
        args.putString(Constants.IMAGE_FILE_NAME, imageFileName);
        args.putDouble(Constants.LAST_POS_LAT, lastKnownLocation.getLatitude());
        args.putDouble(Constants.LAST_POS_LON, lastKnownLocation.getLongitude());
        args.putBoolean(Constants.POI_IS_NEW, true);
        fragment.poi = new Poi();
        fragment.mapController = new MapController(fragment);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Create EditPoit dialog, which is used for UPDATING a Poi
     *
     * @param poi
     */
    public static PoiEditDialog newInstance(Poi poi) {
        PoiEditDialog fragment = new PoiEditDialog();

        Bundle args = new Bundle();
        fragment.poi = poi;
        args.putBoolean(Constants.POI_IS_NEW, false);
        fragment.setArguments(args);
        return fragment;
    }

    //added
    public static PoiEditDialog newInstance(Poi poi, PoiViewDialog dialog) {
        PoiEditDialog fragment = new PoiEditDialog();
        Bundle args = new Bundle();
        fragment.poi = poi;
        args.putBoolean(Constants.POI_IS_NEW, false);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new PoiController();
        imageController = ImageController.getInstance();
        context = getActivity();
        offlineQueueController = OfflineQueueController.getInstance();

        Bundle args = getArguments();
        double lat = args.getDouble(Constants.LAST_POS_LAT);
        double lon = args.getDouble(Constants.LAST_POS_LON);
        isNewPoi = args.getBoolean(Constants.POI_IS_NEW);
        lastKnownLocation = new GeoPoint(lat, lon);

        // set style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

        poiPhotoUploadHandler = event -> {
            switch (event.getType()) {
                case OK:
                    poi = (Poi) event.getModel();
                    DatabaseController.getInstance().sendUpdate(new DatabaseEvent(DatabaseEvent.SyncType.SINGLEPOI, poi));
                    break;
                default:
                    Toast.makeText(context, R.string.image_upload_failed, Toast.LENGTH_LONG).show();
            }
        };

        poiHandler = event -> {
            switch (event.getType()) {
                case OK:
                    if (isNewPoi) {
                        poi = (Poi) event.getModel();
                        //Poi image has to be uploaded after the poi is saved
                        String imagePath = MapFragment.photoPath;
                        Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
                        Uri uri = ImageController.getInstance().getImageUri(getActivity(), imageBitmap);
                        ImageController.getInstance().setAndSaveCorrectOrientation(imageBitmap, uri, new File(imagePath));

                        controller.uploadImage(new File(imagePath), poi, poiPhotoUploadHandler);
                    }
                    Toast.makeText(getActivity(), R.string.poi_successful_saving, Toast.LENGTH_LONG).show();
                    fillInDataFromExistingPoi();

                    ProfileFragment fragment = (ProfileFragment) getFragmentManager().findFragmentByTag("ProfileFragment");
                    if(fragment != null){
                        fragment.setupPOIs(fragment.getView());
                    }

                    PoiViewDialog viewDialog = (PoiViewDialog) getFragmentManager()
                                                                .findFragmentByTag(Constants.DISPLAY_FEEDBACK_DIALOG);
                    
                    if(viewDialog != null){
                        viewDialog.onResume();
                    }

                    dismiss();
                    break;
                case NETWORK_ERROR:
                    AddPoiCommand cmd = new AddPoiCommand(this.poi, new File(MapFragment.photoPath));
                    offlineQueueController.addCommand(cmd);
                    Toast.makeText(context, R.string.in_queue, Toast.LENGTH_SHORT).show();
                    dismiss();
                    break;
                default:
                    Toast.makeText(context, R.string.msg_not_saved, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_poi_edit, container, false);

        titleEditText = (EditText) view.findViewById(R.id.poi_title);
        titleTextLayout = (TextInputLayout) view.findViewById(R.id.poi_title_layout);
        descriptionEditText = (EditText) view.findViewById(R.id.poi_description);
        typeSpinner = (Spinner) view.findViewById(R.id.poi_type_spinner);
        modeSpinner = (Spinner) view.findViewById(R.id.poi_mode_spinner);

        buttonSave = (ImageButton) view.findViewById(R.id.poi_save_button);
        buttonCancel = (ImageButton) view.findViewById(R.id.dialog_edit_poi_cancel_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPublicationModeControls();
        initPublicationTypeControls();
        initActionControls();
        if (!isNewPoi) {
            fillInDataFromExistingPoi();
        }else{
            fillInDataFromAddress();
        }

        //handle keyboard closing
        view.findViewById(R.id.rootLayout).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            return true;
        });
    }



    /**
     * initializes the actions of the mode (private / public) spinner, which sets the current mode of the poi in case of changing of the user
     */
    private void initPublicationModeControls() {
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // position 0 == public, position 1 == private

                //From public to private: set publish to true
                publish = !poi.isPublic() && position == 0;
                poi.setPublic(position == 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    /**
     * initializes the actions of the Type spinner, which sets the current type of the poi in case of changing of the user
     */
    private void initPublicationTypeControls() {
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                poi.setType(position+1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * initializes the actions of all the image buttons (save and cancel) of the fragment
     */
    private void initActionControls() {
        buttonSave.setOnClickListener(v -> {
            if (titleEditText != null && titleEditText.getText() != null) {
                String title = titleEditText.getText().toString();
                poi.setTitle(title);
            }

            if (poi.getTitle().isEmpty()) {
                titleTextLayout.setError(getString(R.string.message_add_title));
                return;
            }
            if (descriptionEditText != null && descriptionEditText.getText() != null) {
                String description = descriptionEditText.getText().toString();
                poi.setDescription(description);
            }

            if (isNewPoi) {
                poi.setLatitude((float) lastKnownLocation.getLatitude());
                poi.setLongitude((float) lastKnownLocation.getLongitude());

                controller.saveNewPoi(this.poi, poiHandler);
            } else {
                File poiImage = new File(this.poi.getImageById(1).getLocalPath());
                if (publish) {
                    controller.uploadImage(poiImage, poi, controllerEvent -> {
                        switch (controllerEvent.getType()) {
                            case OK:
                                controller.updatePoi(poi, poiHandler);
                                break;
                            default:
                                poiHandler.onResponse(new ControllerEvent(EventType.getTypeByCode(500)));
                        }
                    });
                } else {
                    controller.updatePoi(this.poi, poiHandler);
                }
            }

        });

        buttonCancel.setOnClickListener(view -> dismiss());
    }

    /**
     * Prefills all data from a existing Poi into the form for the user
     */
    private void fillInDataFromExistingPoi() {
        titleEditText.setText(this.poi.getTitle());
        descriptionEditText.setText(this.poi.getDescription());
        typeSpinner.setSelection((int) this.poi.getType());
        if (poi.isPublic()) {
            modeSpinner.setSelection(0); // public
        } else {
            modeSpinner.setSelection(1); // private
        }
    }

    /**
     * Prefills all data from address
     */
    private void fillInDataFromAddress(){
        typeSpinner.setSelection(0);
        mapController.searchCoordinates(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(),1, controllerEvent -> {
            AddressPoint addressPoint = (AddressPoint) controllerEvent.getModel();
            if (addressPoint == null){
                Log.d(TAG, "Is null");
                return;
            }
            StringBuilder nameProposal = new StringBuilder();
            boolean isFirst = true;
            if (addressPoint.getName() != null){
                nameProposal.append(addressPoint.getName());
                isFirst = false;
            }
            if (addressPoint.getName() == null && addressPoint.getRoad() != null){
                if (!isFirst){
                    nameProposal.append(", ");
                }
                nameProposal.append(addressPoint.getRoad());
                isFirst = false;
            }
            if (addressPoint.getVillage() != null){
                if (!isFirst){
                    nameProposal.append(", ");
                }
                nameProposal.append(addressPoint.getVillage());
                isFirst = false;
            }
            if (addressPoint.getCity() != null){
                if (!isFirst){
                    nameProposal.append(", ");
                }
                nameProposal.append(addressPoint.getCity());
            }else if (addressPoint.getState() != null){
                if (!isFirst){
                    nameProposal.append(", ");
                }
                nameProposal.append(addressPoint.getState());
            }
            titleEditText.setText(nameProposal.toString());
        });
    }
}
