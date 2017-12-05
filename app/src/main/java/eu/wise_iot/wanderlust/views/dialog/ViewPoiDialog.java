package eu.wise_iot.wanderlust.views.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
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

import java.io.File;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiTypeDao;
import eu.wise_iot.wanderlust.models.Old.Feedback;
import eu.wise_iot.wanderlust.services.FeedbackService;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.PoiFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ViewPoiDialog:
 * @author Fabian Schwander
 * @license MIT
 */
public class ViewPoiDialog extends DialogFragment {
    private static final String TAG = "ViewPoiDialog";
    private Activity context;

    private ImageView feedbackImage;
    private ImageView displayModeImage;
    private Button closeDialogButton;
    private TextView titelTextView;
    private TextView descriptionTextView;

    private long feedbackId;
    private Feedback feedback;

    private static PoiController controller;

    public static ViewPoiDialog newInstance(OverlayItem overlayItem) {

        ViewPoiDialog dialog = new ViewPoiDialog();
        dialog.setStyle(R.style.my_no_border_dialog_theme, R.style.AppTheme);
        long feedbackId = Long.valueOf(overlayItem.getUid());
        Bundle args = new Bundle();
        args.putLong(Constants.POI_ID, feedbackId);
        dialog.setArguments(args);
        return dialog;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        controller = new PoiController();
//        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); // TODO: added for screen orientation change (see TODO below)

        Bundle args = getArguments();
        feedbackId = args.getLong(Constants.POI_ID);
        setRetainInstance(true);
//        loadFeedbackById(feedbackId);
        loadPoiById(feedbackId);
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
        return inflater.inflate(R.layout.dialog_view_poi, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayModeImage = (ImageView) view.findViewById(R.id.poi_mode_private_image);
        titelTextView = (TextView) view.findViewById(R.id.poi_title_text_view);
        descriptionTextView = (TextView) view.findViewById(R.id.poi_description_text_view);
        closeDialogButton = (Button) view.findViewById(R.id.poi_close_dialog_button);
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


    private void loadPoiById(long id){

        controller.getPoiById(id, new FragmentHandler() {
            @Override
            public void onResponse(Event event) {
                switch (event.getType()){
                    case OK:
                        //TODO was passiert wenn gefunden..
                        Poi poi = (Poi) event.getModel();
//                        controller.getImages(poi.getPoi_id(), new FragmentHandler() {
//                            @Override
//                            public void onResponse(Event event) {
//                                switch (event.getType()){
//                                    case OK:
//                                        // first (id = 0) image downloaded
//                                        Poi.ImageInfo imageInfo = (Poi.ImageInfo) event.getModel();
//                                        File image = imageInfo.getImage();
//
//                                        Picasso.with(activity).load(image).into(feedbackImage);
//
//                                        break;
//                                    default:
//                                }
//                            }
//                        });

                        //File image = poi.getImagePath().get(0).getImage();
                        controller.getImages(poi, new FragmentHandler() {
                            @Override
                            public void onResponse(Event event) {
                                switch (event.getType()){
                                    case OK:
                                        List<File> images = (List<File>) event.getModel();
                                        Picasso.with(context).load(images.get(0)).into(feedbackImage);
                                        break;
                                    default:

                                }

                            }
                        });
                        //Picasso.with(activity).load(image).into(feedbackImage);
                        titelTextView.setText(poi.getTitle()); // TODO: get real title
                        descriptionTextView.setText(poi.getDescription());



                        // get all images of poi
//                        for (Poi.ImageInfo imageInfo : poi.getImagePath()) {
//                            File image = new File(imageInfo.getPath());
//
//                            controller.downloadImage(poi.getPoi_id(), imageInfo.getId(), new FragmentHandler() {
//                                @Override
//                                public void onResponse(Event event) {
//                                    switch (event.getType()) {
//                                        case OK:
//                                            // image zuweisen
//                                    }
//                                }
//                            });
//                        }


                        //poi types which have to go to a select box or somthing:
                        List<PoiType> poiTypes =  controller.getTypes();

                        break;
                    default:
                        //TODO was passiert wenn nicht gefunden..
                        //Careful getModel() will return null!
                }
            }
        });
    }


//    //TODO remove, feedback doesnt exist anymore
//    private void loadFeedbackById(long id) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Defaults.URL_SERVER)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        FeedbackService service = retrofit.create(FeedbackService.class);
//        Call<Feedback> call = service.loadFeedbackById(id);
//        call.enqueue(new Callback<Feedback>() {
//            @Override
//            public void onResponse(Call<Feedback> call, Response<Feedback> response) {
//                if (response.isSuccessful()) {
//                    feedback = response.body();
//                    titelTextView.setText("Titel"); // TODO: get real title
//                    descriptionTextView.setText(feedback.getDescription());
//                    if (feedback.getDisplayMode() == Constants.MODE_PRIVATE) {
//                        Picasso.with(activity).load(R.drawable.image_msg_mode_private).fit().into(displayModeImage);
//                    }
//
//                    int imageId = activity.getResources().getIdentifier(feedback.getImageNameWithoutSuffix(), "drawable", activity.getPackageName());
//                    if (imageId != 0) {
//                        // todo: work with .error() from the Picasso library
//                        Picasso.with(activity).load(imageId).into(feedbackImage);
//                    } else {
//                        Picasso.with(activity).load(R.drawable.image_msg_file_missing).into(feedbackImage);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Feedback> call, Throwable t) {
//                Toast.makeText(activity, R.string.msg_e_feedback_loading_error, Toast.LENGTH_LONG).show();
//                Log.d(TAG, t.getMessage());
//            }
//        });
//    }
}
