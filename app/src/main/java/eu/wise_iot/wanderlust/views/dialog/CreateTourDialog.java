package eu.wise_iot.wanderlust.views.dialog;

import android.Manifest;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EquipmentController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.controllers.TourKitController;
import eu.wise_iot.wanderlust.models.DatabaseModel.AddressPoint;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourKit;
import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
import eu.wise_iot.wanderlust.views.controls.EquipmentCompletionView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;

/** tourkitExtraKitInput
 * CreateTour:
 *
 * @author Joshua Meier
 * @license MIT
 */
public class CreateTourDialog extends DialogFragment {
    private static final String TAG = "CreateTourDialog";
    private FragmentHandler<Trip> createTourhandler;
    private FragmentHandler<Trip> uploadPhotoHandler;
    private FragmentHandler<TourKit> tourKitHandler;
    private FragmentHandler<Tour> getCreatedTourHandler;
    private FragmentHandler<Trip> saveTourLocalHandler;
    private FragmentHandler<Tour> updateTourHandler;

    private ArrayList<GeoPoint> trackedTour;
    private List<Region> regions;
    private List<String> regionNames;

    private Tour tour;
    private Trip currentTrip;
    private EditText titleEditText;
    private TextInputLayout titleTextLayout;
    private EditText descriptionEditText;
    private Spinner difficultySpinner;
    private Spinner publicSpinner;
    private Spinner regionSpinner;
    private CheckBox summerCheckBox;
    private CheckBox fallCheckBox;
    private CheckBox springCheckBox;
    private CheckBox winterCheckBox;

    private ImageView tourImageDisplay;
    private Button uploadImageBtn;
    private ImageButton buttonSave;
    private ImageButton buttonCancel;
    private TourController tourController;
    private MapController mapController;
    private boolean isNewTour;
    private boolean publish;

    private Bitmap imageBitmap;
    private String realPath;
    private EquipmentCompletionView extraTourKitInput;


    private ImageController imageController;
    private EquipmentController equipmentController;
    private TourKitController tourKitController;



    /**
     * Create a NEW tour dialog to create tour without further information
     *
     * @param trackedTour The GeoPoints of the tracked tour
     */
    public static CreateTourDialog newInstance(ArrayList<GeoPoint> trackedTour) {
        CreateTourDialog createTourDialog = new CreateTourDialog();
        Bundle args = new Bundle();
        args.putSerializable(Constants.CREATE_TOUR_TRACK, trackedTour);
        args.putBoolean(Constants.CREATE_TOUR_IS_NEW, true);
        createTourDialog.tour = new Tour();
        createTourDialog.setArguments(args);
        return createTourDialog;
    }

    /**
     * Create a EDIT tour dialog to edit an already existing tour
     *
     * @param tour the tour which needs to be edited
     */
    public static CreateTourDialog newInstance(Tour tour) {
        CreateTourDialog createTourDialog = new CreateTourDialog();
        Bundle args = new Bundle();
        args.putBoolean(Constants.CREATE_TOUR_IS_NEW, false);
        createTourDialog.tour = tour;
        createTourDialog.setArguments(args);
        return createTourDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tourController = new TourController(tour);
        mapController = new MapController(this);
        tourKitController = TourKitController.createInstance(getActivity());
        imageController = ImageController.getInstance();
        equipmentController = EquipmentController.createInstance(getActivity());
        regions = tourController.getAllRegions();
        regionNames = new ArrayList<>();

        for (Region region : regions) {
            regionNames.add(region.getName());
        }

        Bundle args = getArguments();
        isNewTour = args.getBoolean(Constants.CREATE_TOUR_IS_NEW);
        if (isNewTour) {
            trackedTour = (ArrayList<GeoPoint>) args.getSerializable(Constants.CREATE_TOUR_TRACK);
            if(trackedTour != null) {
                String polyline = PolyLineEncoder.encode(trackedTour, 10);
                tour.setPolyline(polyline);
            }
        }

        // set style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

        createTourhandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                currentTrip = controllerEvent.getModel();
                tourController.getTourById(currentTrip.getTour(), getCreatedTourHandler);
            } else {
                Toast.makeText(getActivity(), R.string.err_msg_error_occured, Toast.LENGTH_SHORT).show();
            }
        };

        uploadPhotoHandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                Toast.makeText(getActivity(), R.string.create_tour_saved, Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                if(isNewTour){
                    tourController.deleteTrip(currentTrip, emptyEvent -> {});
                }
                Toast.makeText(getActivity(), R.string.err_msg_error_occured, Toast.LENGTH_SHORT).show();
            }
        };


        tourKitHandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                Log.e(TAG, "Tour Kit ID " + controllerEvent.getModel() + " erfolgreich gespeichert");
            } else {
                Log.e(TAG, "Tour Kit ID " + controllerEvent.getModel() + " NICHT gespeichert");
            }
        };

        getCreatedTourHandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                Tour currentTour = controllerEvent.getModel();
                currentTour.setInternal_id(0);
                tourController.addTour(saveTourLocalHandler, currentTour);
                for(Equipment eq : extraTourKitInput.getObjects()){
                    TourKit tourKit = new TourKit(0, 0, currentTour.getTour_id(), eq.getEquip_id());
                    tourKitController.addEquipmentToTour(tourKit, tourKitHandler);
                }


            } else {
                Toast.makeText(getActivity(), R.string.err_msg_error_occured, Toast.LENGTH_SHORT).show();
            }
        };

        saveTourLocalHandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                tourController.uploadImage(new File(realPath), uploadPhotoHandler);
            } else {
                Toast.makeText(getActivity(), R.string.err_msg_error_occured, Toast.LENGTH_SHORT).show();
            }
        };

        updateTourHandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                if(realPath != null){
                    tourController.uploadImage(new File(realPath), uploadPhotoHandler);
                } else {
                    Toast.makeText(getActivity(), R.string.create_tour_update_successful, Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            } else {
                Toast.makeText(getActivity(), R.string.err_msg_error_occured, Toast.LENGTH_SHORT).show();
            }
        };




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_tour, container, false);

        titleEditText = view.findViewById(R.id.tourOVTourTitle);
        titleTextLayout = view.findViewById(R.id.tour_title_layout);
        descriptionEditText = view.findViewById(R.id.tour_description);
        publicSpinner = view.findViewById(R.id.tour_is_public);
        difficultySpinner = view.findViewById(R.id.tourOVTourDifficulty);
        buttonSave = view.findViewById(R.id.tour_save_button);
        uploadImageBtn = view.findViewById(R.id.upload_image_btn);
        buttonCancel = view.findViewById(R.id.tour_return_button);
        regionSpinner = view.findViewById(R.id.tour_region);
        winterCheckBox = view.findViewById(R.id.create_tour_checkbox_winter);
        summerCheckBox = view.findViewById(R.id.create_tour_checkbox_summer);
        fallCheckBox = view.findViewById(R.id.create_tour_checkbox_fall);
        springCheckBox = view.findViewById(R.id.create_tour_checkbox_spring);
        tourImageDisplay = view.findViewById(R.id.tour_image);
        extraTourKitInput = view.findViewById(R.id.tourkitExtraKitInput);


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, regionNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(spinnerArrayAdapter);

        extraTourKitInput.setThreshold(1);
        extraTourKitInput.allowDuplicates(false);
        ArrayAdapter<Equipment> equipmentAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, equipmentController.getExtraEquipmentList());
        extraTourKitInput.setAdapter(equipmentAdapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1212:
                    onActionResultGallery(data);
                    break;
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();
        fillInDataFromFirstGeopoint();
        if (!isNewTour) {
            fillInDataFromExistingTour();
        }

        //handle keyboard closing
        view.findViewById(R.id.rootLayout).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            return true;
        });
    }

    /**
     * initializes the actions of all the image buttons (save and cancel) of the fragment
     */
    private void initActionControls() {
        buttonSave.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            tour.setTitle(title);

            if (tour.getTitle().isEmpty()) {
                titleTextLayout.setError(getString(R.string.message_add_title));
                return;
            }

            if (realPath == null) {
                Toast.makeText(getActivity(), R.string.create_tour_photo_required, Toast.LENGTH_SHORT).show();
                return;
            }

            String description = descriptionEditText.getText().toString();
            tour.setDescription(description);

            tour.setDifficulty(difficultySpinner.getSelectedItemPosition() + 1);

            publish = !tour.isPublic() && publicSpinner.getSelectedItemPosition() == 0;
            tour.setPublic(publicSpinner.getSelectedItemPosition() == 0);
            tour.setRegion(regions.get((int) regionSpinner.getSelectedItemId()).getRegion_id());

            ArrayList<String> seasons = new ArrayList<>();
            if (springCheckBox.isChecked()) seasons.add("spring");
            if (winterCheckBox.isChecked()) seasons.add("winter");
            if (fallCheckBox.isChecked()) seasons.add("fall");
            if (summerCheckBox.isChecked()) seasons.add("summer");
            tour.setSeasons(seasons);

            if (seasons.isEmpty()) {
                Toast.makeText(getActivity(), R.string.create_tour_no_seasons, Toast.LENGTH_SHORT).show();
                return;
            }

            if (isNewTour) {
                tourController.createTour(createTourhandler);
            } else {
                tourController.updateTour(updateTourHandler);
            }
        });

        buttonCancel.setOnClickListener(view -> {
            if (isNewTour) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.create_tour_not_saved)
                        .setMessage(R.string.create_tour_not_save_tour)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, positiveButton) -> dismiss())
                        .setNegativeButton(android.R.string.no, null).show();
            } else {
                dismiss();
            }

        });

        uploadImageBtn.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(getActivity(), getString(R.string.msg_picture_not_saved),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Fills all data from a existing Tour into the form for the user
     */
    private void fillInDataFromExistingTour() {
        titleEditText.setText(tour.getTitle());
        descriptionEditText.setText(tour.getDescription());
        difficultySpinner.setSelection((int) tour.getDifficulty() - 1);
        if (tour.isPublic()) {
            publicSpinner.setSelection(0); // public
        } else {
            publicSpinner.setSelection(1); // private
        }
    }

    private void fillInDataFromFirstGeopoint() {
        GeoPoint firstTrackedPoint = trackedTour.get(0);
        mapController.searchCoordinates(firstTrackedPoint.getLatitude(), firstTrackedPoint.getLongitude(), 1, (ControllerEvent controllerEvent) -> {
            AddressPoint addressPoint = (AddressPoint) controllerEvent.getModel();
            if (addressPoint != null && addressPoint.getState() != null) {
                Region region = tourController.getRegionFromString(addressPoint.getState());
                if (region != null) {
                    for (Region region1 : regions) {
                        if (region1.getRegion_id() == region.getRegion_id()) {
                            regionSpinner.setSelection(regions.indexOf(region1));
                        }
                    }
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        if (galleryIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(galleryIntent, "Complete action using"), 1212);
        }
    }

    private void onActionResultGallery(Intent data) {

        Uri returnUri = data.getData();

        if (returnUri == null) {
            Bundle extras = data.getExtras();
            returnUri = imageController.getImageUri(getActivity().getApplicationContext(), (Bitmap) extras.get("data"));
        }

        realPath = imageController.getRealPathFromURI(returnUri, getActivity());
        File image = new File(realPath);
        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), returnUri);
            imageController.setAndSaveCorrectOrientation(imageBitmap, returnUri, image);
            imageBitmap = imageController.resize(imageBitmap, 1024);
            if(image.length() > 500_000){
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(image));
                if(image.length() > 500_000){
                    //Still to high quality
                    Toast.makeText(getActivity(), R.string.image_upload_failed, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (IOException e) {
            if (BuildConfig.DEBUG) Log.d(TAG, e.getMessage());
        }

        if (imageBitmap != null) {
            Picasso.with(getActivity().getApplicationContext())
                    .load(returnUri)
                    .into(tourImageDisplay);
        }
    }
}
