package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.Old.Feedback;
import eu.wise_iot.wanderlust.services.FeedbackService;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.MapFragment;
import eu.wise_iot.wanderlust.views.MyMapOverlays;
import eu.wise_iot.wanderlust.views.PoiFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * EditPoiDialog:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class EditPoiDialog extends DialogFragment {
    private static final String TAG = "EditPoiDialog";
    private Context context;

    private String imageFileName;
    private GeoPoint lastKnownLocation;
    private boolean displayMode;
    private int feedbackType;
    private String title; // todo: has to be added to model
    private String description;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner typeSpinner;
    private Button buttonSave;
    private Button buttonCancel;
    private Feedback feedback;
    private PoiController controller;
    private static MyMapOverlays mapOverlays;

    public static EditPoiDialog newInstance(String imageFileName, GeoPoint lastKnownLocation, MyMapOverlays overlays) {
        EditPoiDialog fragment = new EditPoiDialog();
        Bundle args = new Bundle();
        args.putString(Constants.IMAGE_FILE_NAME, imageFileName);
        args.putDouble(Constants.LAST_POS_LAT, lastKnownLocation.getLatitude());
        args.putDouble(Constants.LAST_POS_LON, lastKnownLocation.getLongitude());
        fragment.setArguments(args);
        mapOverlays = overlays;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new PoiController();
        Bundle args = getArguments();
        imageFileName = args.getString(Constants.IMAGE_FILE_NAME);
        double lat = args.getDouble(Constants.LAST_POS_LAT);
        double lon = args.getDouble(Constants.LAST_POS_LON);
        lastKnownLocation = new GeoPoint(lat, lon);
        context = getActivity();
        // set style and options menu
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_edit_poi, container);
        titleEditText = (EditText) view.findViewById(R.id.poi_title);
        descriptionEditText = (EditText) view.findViewById(R.id.poi_description);
        typeSpinner = (Spinner) view.findViewById(R.id.poi_typeSpinner);
        buttonSave = (Button) view.findViewById(R.id.dialog_post_feedback_button_save);
        buttonCancel = (Button) view.findViewById(R.id.dialog_post_feedback_button_cancel);
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
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        displayMode = Constants.MODE_PUBLIC;
                        break;
                    case 1:
                        displayMode = Constants.MODE_PRIVATE;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context, R.string.msg_please_choose_mode, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initPublicationTypeControls() {
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        feedbackType = Constants.TYPE_VIEW;
                        break;
                    case 1:
                        feedbackType = Constants.TYPE_RESTAURANT;
                        break;
                    case 2:
                        feedbackType = Constants.TYPE_REST_AREA;
                        break;
                    case 3:
                        feedbackType = Constants.TYPE_FLORA_FAUNA;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context, R.string.msg_please_choose_mode, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initActionControls() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Poi poi = new Poi();

                if (titleEditText != null && titleEditText.getText() != null) {
                    title = titleEditText.getText().toString();
                }
                if (descriptionEditText != null && descriptionEditText.getText() != null) {
                    description = descriptionEditText.getText().toString();
                    poi.setDescription(description);
                }

                poi.setType(feedbackType);
                poi.setLatitude(lastKnownLocation.getLatitude());
                poi.setLongitude(lastKnownLocation.getLongitude());
                poi.setPublic(displayMode);

                controller.saveNewPoi(poi, new FragmentHandler() {
                    @Override
                    public void onResponse(Event event) {
                        switch (event.getType()) {
                            case OK:

                                Poi poi = (Poi) event.getModel();
                                //Poi image has to be uploaded after the poi is saved
                                controller.uploadImage(new File(MapFragment.photoPath), poi.getPoi_id(), new FragmentHandler() {
                                    @Override
                                    public void onResponse(Event event) {
                                        switch (event.getType()) {
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

                                mapOverlays.addPoiToOverlay(poi);
                                break;
                            default:
                                Toast.makeText(context, R.string.msg_not_saved, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dismiss();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_fragment_layer_menu, menu);
    }
}
