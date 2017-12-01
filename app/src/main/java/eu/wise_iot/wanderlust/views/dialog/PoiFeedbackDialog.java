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
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.models.Old.Feedback;
import eu.wise_iot.wanderlust.services.FeedbackService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * PoiFeedbackDialog:
 * @author Fabian Schwander
 * @license MIT
 */
public class PoiFeedbackDialog extends DialogFragment {
    private static final String TAG = "PoiFeedbackDialog";
    private Context context;

    private String imageFileName;
    private GeoPoint lastKnownLocation;
    private int displayMode;
    private int feedbackType;
    private String title; // todo: has to be added to model
    private String description;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private Spinner typeSpinner;
    private Button buttonSave;
    private Button buttonCancel;
    private Feedback feedback;

    public static PoiFeedbackDialog newInstance(String imageFileName, GeoPoint lastKnownLocation) {
        PoiFeedbackDialog fragment = new PoiFeedbackDialog();
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
        View view = inflater.inflate(R.layout.dialog_fragment_poi, container);
        titleEditText = (EditText) view.findViewById(R.id.title);
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
                if (titleEditText.getText() != null) {
                    title = titleEditText.getText().toString();
                }
                if (descriptionEditText.getText() != null) {
                   description = descriptionEditText.getText().toString();
                }
                onSaveFeedbackButtonClicked();
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

    public void onSaveFeedbackButtonClicked() {
        double lat = lastKnownLocation.getLatitude();
        double lon = lastKnownLocation.getLongitude();
        feedback = new Feedback(displayMode, feedbackType, lat, lon, imageFileName, description);
        sendSaveFeedbackNetworkRequest(feedback);
    }

    private void sendSaveFeedbackNetworkRequest(final Feedback feedback) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Defaults.URL_SERVER)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        FeedbackService client = retrofit.create(FeedbackService.class);
        Call<Feedback> call = client.saveNewFeedback(feedback);
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                Toast.makeText(context, R.string.msg_visible_after_refresh, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "photo saved and response received: " + response.isSuccessful());
//                mapOverlays.addFeedbackIconToOverlay(PoiFeedbackDialog.this.feedback); // FIXME: throws NPE
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Toast.makeText(context, R.string.msg_not_saved, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "saving photo failed");
                Log.e(TAG, t.getMessage());
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_fragment_layer_menu, menu);
    }
}
