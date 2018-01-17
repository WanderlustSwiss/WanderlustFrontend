package eu.wise_iot.wanderlust.views.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.Locale;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;

/**
 * ViewPoiDialog:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class ViewPoiDialog extends DialogFragment {
    private static final String TAG = "ViewPoiDialog";
    //Assume there is only 1 ViewPoiDialog
    private static Poi currentPoi;
    private Activity context;
    private ImageView poiImage;
    private ImageView displayModeImage;
    private TextView typeTextView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private ImageButton closeDialogButton;
    private ImageButton editPoiButton;
    private ImageButton deletePoiButton;
    private long poiId;
    private PoiController controller;

    /**
     * Create a ViewPoiDialog from a Poi object
     *
     * @param poi
     */
    public static ViewPoiDialog newInstance(Poi poi) {
        ViewPoiDialog dialog = new ViewPoiDialog();
        currentPoi = poi;
        dialog.setStyle(R.style.my_no_border_dialog_theme, R.style.AppTheme);
        long poiId = poi.getPoi_id();
        Bundle args = new Bundle();
        args.putLong(Constants.POI_ID, poiId);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        controller = new PoiController();

        Bundle args = getArguments();
        poiId = args.getLong(Constants.POI_ID);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_poi, container, false);
        poiImage = (ImageView) view.findViewById(R.id.poi_image);
        displayModeImage = (ImageView) view.findViewById(R.id.poi_mode_private_image);
        typeTextView = (TextView) view.findViewById(R.id.poi_type_text_view);
        titleTextView = (TextView) view.findViewById(R.id.poi_title_text_view);
        dateTextView = (TextView) view.findViewById(R.id.poi_date_text_view);
        descriptionTextView = (TextView) view.findViewById(R.id.poi_description_text_view);
        closeDialogButton = (ImageButton) view.findViewById(R.id.poi_close_dialog_button);
        editPoiButton = (ImageButton) view.findViewById(R.id.poi_edit_button);
        deletePoiButton = (ImageButton) view.findViewById(R.id.poi_delete_button);

        if (controller.isOwnerOf(currentPoi)) {
            this.showControlsForOwner();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called // requestCode: " + requestCode + " / resultCode: " + resultCode);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();
        fillOutPoiView();
    }

    private void initActionControls() {
        closeDialogButton.setOnClickListener(v -> {
            // dismisses the current dialog view
            dismiss();
        });

        editPoiButton.setOnClickListener(v -> {
            if (controller.isOwnerOf(currentPoi)) {
                EditPoiDialog dialog = EditPoiDialog.newInstance(this.currentPoi);
                dialog.show(getFragmentManager(), Constants.CREATE_FEEDBACK_DIALOG);
            }
        });

        deletePoiButton.setOnClickListener(v -> {
            if (controller.isOwnerOf(currentPoi)) {
                ConfirmDeletePoiDialog dialog = ConfirmDeletePoiDialog.newInstance(context, controller, currentPoi, getString(R.string.message_confirm_delete_poi));
                dialog.show(getFragmentManager(), Constants.CONFIRM_DELETE_POI_DIALOG);
                dismiss();
            }
        });
    }

    private void fillOutPoiView() {
        controller.getImages(currentPoi, new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                List<File> images = (List<File>) controllerEvent.getModel();
                if (images.size() > 0) {
                    Picasso.with(context).load(images.get(0)).resize(350, 250).centerCrop().into(poiImage);
                }
            }
        });

        if (!currentPoi.isPublic()) {
            Picasso.with(context).load(R.drawable.image_msg_mode_private).fit().into(displayModeImage);
        }


        String[] typeValues = getResources().getStringArray(R.array.dialog_feedback_spinner_type);
        typeTextView.setText(typeValues[(int) currentPoi.getType()]);

        titleTextView.setText(currentPoi.getTitle());

        dateTextView.setText(currentPoi.getCreatedAt(Locale.GERMAN));
        descriptionTextView.setText(currentPoi.getDescription());
    }

    private void showControlsForOwner() {
        editPoiButton.setVisibility(View.VISIBLE);
        deletePoiButton.setVisibility(View.VISIBLE);
    }

    public static Poi getCurrentPoi() {
        return currentPoi;
    }

    public static void setCurrentPoi(Poi currentPoi) {
        ViewPoiDialog.currentPoi = currentPoi;
    }

    public PoiController getController() {
        return controller;
    }

    public void setController(PoiController controller) {
        this.controller = controller;
    }
}
