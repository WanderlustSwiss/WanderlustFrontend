package eu.wise_iot.wanderlust.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import org.osmdroid.util.GeoPoint;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.model.Feedback;
import eu.wise_iot.wanderlust.service.FeedbackService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * CreateFeedbackDialog:
 * @author Fabian Schwander
 * @license MIT
 */
public class CreateFeedbackDialog extends DialogFragment {
    private static final String TAG = "CreateFeedbackDialog";
    private Context context;

    private String imageFileName;
    private GeoPoint lastKnownLocation;
    private int displayMode;
    private int feedbackType;
    private String description;

    private EditText descriptionEditText;
    private Spinner publicationModeSpinner;
    private RadioGroup feedbackTypeRadioGroup;
    private Button buttonSave;
    private Button buttonCancel;
    private Feedback feedback;

    public static CreateFeedbackDialog newInstance(String imageFileName, GeoPoint lastKnownLocation) {
        CreateFeedbackDialog fragment = new CreateFeedbackDialog();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_create_feedback, container);
        descriptionEditText = (EditText) view.findViewById(R.id.dialog_post_feedback_description_edit_text);
        publicationModeSpinner = (Spinner) view.findViewById(R.id.publication_mode_spinner);
        feedbackTypeRadioGroup = (RadioGroup) view.findViewById(R.id.feedback_type_radio_group);
        buttonSave = (Button) view.findViewById(R.id.dialog_post_feedback_button_save);
        buttonCancel = (Button) view.findViewById(R.id.dialog_post_feedback_button_cancel);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPublicationModeControls();
        initFeedbackTypeControls();
        initActionControls();
        // prevents closing of dialog when taped on the outside
        setCancelable(false);
    }

    private void initPublicationModeControls() {
        publicationModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void initFeedbackTypeControls() {
        // default value
        feedbackType = Constants.TYPE_POSITIVE;
        feedbackTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_positive:
                        feedbackType = Constants.TYPE_POSITIVE;
                        break;
                    case R.id.radio_button_negative:
                        feedbackType = Constants.TYPE_NEGATIVE;
                        break;
                    case R.id.radio_button_alert:
                        feedbackType = Constants.TYPE_ALERT;
                        break;
                }
            }
        });
    }

    private void initActionControls() {
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//                mapOverlays.addFeedbackIconToOverlay(CreateFeedbackDialog.this.feedback); // FIXME: throws NPE
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Toast.makeText(context, R.string.msg_not_saved, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "saving photo failed");
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
