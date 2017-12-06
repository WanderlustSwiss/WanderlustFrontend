package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.io.File;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.views.MapFragment;
import eu.wise_iot.wanderlust.views.MyMapOverlays;

/**
 * EditPoiDialog:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class EditPoiDialog extends DialogFragment {
    private static final String TAG = "EditPoiDialog";
    private Context context;

    private Poi poi;
    private GeoPoint lastKnownLocation;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner typeSpinner;
    private Spinner modeSpinner;
    private Button buttonSave;
    private Button buttonCancel;

    private PoiController controller;

    public static EditPoiDialog newInstance(String imageFileName, GeoPoint lastKnownLocation) {
        EditPoiDialog fragment = new EditPoiDialog();
        Bundle args = new Bundle();
        args.putString(Constants.IMAGE_FILE_NAME, imageFileName);
        args.putDouble(Constants.LAST_POS_LAT, lastKnownLocation.getLatitude());
        args.putDouble(Constants.LAST_POS_LON, lastKnownLocation.getLongitude());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        poi = new Poi();
        controller = new PoiController();
        context = getActivity();

        Bundle args = getArguments();
        double lat = args.getDouble(Constants.LAST_POS_LAT);
        double lon = args.getDouble(Constants.LAST_POS_LON);
        lastKnownLocation = new GeoPoint(lat, lon);

        // set style and options menu
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_poi, container);

        titleEditText = (EditText) view.findViewById(R.id.poi_title);
        descriptionEditText = (EditText) view.findViewById(R.id.poi_description);
        typeSpinner = (Spinner) view.findViewById(R.id.poi_type_spinner);
        modeSpinner = (Spinner) view.findViewById(R.id.poi_mode_spinner);

        buttonSave = (Button) view.findViewById(R.id.dialog_edit_poi_save_button);
        buttonCancel = (Button) view.findViewById(R.id.dialog_edit_poi_cancel_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPublicationModeControls();
        initPublicationTypeControls();
        initActionControls();
    }

    private void initPublicationModeControls() {
        modeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // set mode public -> first item is default
                        poi.setPublic(true);
                        break;
                    case 1:
                        // set mode private
                        poi.setPublic(false);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initPublicationTypeControls() {
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                poi.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void initActionControls() {
        buttonSave.setOnClickListener(v -> {

            if (titleEditText != null && titleEditText.getText() != null) {
                String title = titleEditText.getText().toString();
                poi.setTitle(title);
            }
            if (descriptionEditText != null && descriptionEditText.getText() != null) {
                String description = descriptionEditText.getText().toString();
                poi.setDescription(description);
            }

            poi.setLatitude((float) lastKnownLocation.getLatitude());
            poi.setLongitude((float) lastKnownLocation.getLongitude());

            controller.saveNewPoi(poi, event -> {
                switch (event.getType()) {
                    case OK:
                        Poi tempPoi = (Poi) event.getModel();
                        //Poi image has to be uploaded after the poi is saved
                        controller.uploadImage(new File(MapFragment.photoPath), tempPoi, new FragmentHandler() {
                            @Override
                            public void onResponse(ControllerEvent controllerEvent) {
                                switch (controllerEvent.getType()) {
                                    case OK:
                                        Toast.makeText(context, "image upload good", Toast.LENGTH_LONG).show();
                                        //TODO what to do if image could be saved
                                        break;
                                    default:
                                        Toast.makeText(context, "image upload failed", Toast.LENGTH_LONG).show();
                                        //TODO what to do if image upload fails
                                }
                            }
                        });
                        DatabaseController.sendUpdate(new DatabaseEvent(DatabaseEvent.SyncType.SINGLEPOI, poi));
                        break;

                    default:
                        Toast.makeText(context, R.string.msg_not_saved, Toast.LENGTH_SHORT).show();
                }
            });
            dismiss();
        });
        buttonCancel.setOnClickListener(view -> dismiss());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_fragment_layer_menu, menu);
    }
}
