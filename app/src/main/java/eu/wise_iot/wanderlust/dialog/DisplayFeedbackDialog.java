package eu.wise_iot.wanderlust.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.osmdroid.views.overlay.OverlayItem;

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
 * DisplayFeedbackDialog:
 * @author Fabian Schwander
 * @license MIT
 */
public class DisplayFeedbackDialog extends DialogFragment {
    private static final String TAG = "DisplayFeedbackDialog";
    private Activity activity;

    private ImageView feedbackImage;
    private ImageView displayModeImage;
    private Button closeDialogButton;
    private TextView descriptionTextView;

    private long feedbackId;
    private Feedback feedback;

    public static DisplayFeedbackDialog newInstance(OverlayItem overlayItem) {
        DisplayFeedbackDialog fragment = new DisplayFeedbackDialog();
        fragment.setStyle(R.style.my_no_border_dialog_theme, R.style.AppTheme);
        long feedbackId = Long.valueOf(overlayItem.getUid());
        Bundle args = new Bundle();
        args.putLong(Constants.FEEDBACK_ID, feedbackId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); // TODO: added for screen orientation change (see TODO below)

        Bundle args = getArguments();
        feedbackId = args.getLong(Constants.FEEDBACK_ID);
        setRetainInstance(true);
        loadFeedbackById(feedbackId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // make the background of the dialog transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_display_feedback, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        feedbackImage = (ImageView) view.findViewById(R.id.feedback_image);
        displayModeImage = (ImageView) view.findViewById(R.id.mode_private_image);
        descriptionTextView = (TextView) view.findViewById(R.id.description_text_view);
        closeDialogButton = (Button) view.findViewById(R.id.closeDialogButton);
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    // TODO: enable screen orientation in this dialog so that landscape pictures can be displayed full size
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//    }
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        outState.putLong("id", feedbackId);
//        super.onSaveInstanceState(outState);
//    }
//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if (savedInstanceState != null) {
//            feedbackId = savedInstanceState.getLong("id");
//        }
//    }

    private void loadFeedbackById(long id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Defaults.URL_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedbackService service = retrofit.create(FeedbackService.class);
        Call<Feedback> call = service.loadFeedbackById(id);
        call.enqueue(new Callback<Feedback>() {
            @Override
            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
                if (response.isSuccessful()) {
                    feedback = response.body();
                    descriptionTextView.setText(feedback.getDescription());
                    if (feedback.getDisplayMode() == Constants.MODE_PRIVATE) {
                        Picasso.with(activity).load(R.drawable.image_msg_mode_private).fit().into(displayModeImage);
                    }

                    int imageId = activity.getResources().getIdentifier(feedback.getImageNameWithoutSuffix(), "drawable", activity.getPackageName());
                    if (imageId != 0) {
                        // todo: work with .error() from the Picasso library
                        Picasso.with(activity).load(imageId).into(feedbackImage);
                    } else {
                        Picasso.with(activity).load(R.drawable.image_msg_file_missing).into(feedbackImage);
                    }
                }
            }

            @Override
            public void onFailure(Call<Feedback> call, Throwable t) {
                Toast.makeText(activity, R.string.msg_e_feedback_loading_error, Toast.LENGTH_LONG).show();
                Log.d(TAG, t.getMessage());
            }
        });
    }
}
