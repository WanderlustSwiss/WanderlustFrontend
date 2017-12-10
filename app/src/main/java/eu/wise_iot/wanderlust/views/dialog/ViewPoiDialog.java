package eu.wise_iot.wanderlust.views.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.osmdroid.views.overlay.OverlayItem;

import java.io.File;
import java.util.List;
import java.util.Locale;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;

/**
 * ViewPoiDialog:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class ViewPoiDialog extends DialogFragment {
    private static final String TAG = "ViewPoiDialog";
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

    public static ViewPoiDialog newInstance(OverlayItem overlayItem) {
        ViewPoiDialog dialog = new ViewPoiDialog();
        dialog.setStyle(R.style.my_no_border_dialog_theme, R.style.AppTheme);
        long poiId = Long.valueOf(overlayItem.getUid());
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
        loadPoiById(poiId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
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
        poiImage = (ImageView) view.findViewById(R.id.poi_image);
        displayModeImage = (ImageView) view.findViewById(R.id.poi_mode_private_image);
        typeTextView = (TextView) view.findViewById(R.id.poi_type_text_view);
        titleTextView = (TextView) view.findViewById(R.id.poi_title_text_view);
        dateTextView = (TextView) view.findViewById(R.id.poi_date_text_view);
        descriptionTextView = (TextView) view.findViewById(R.id.poi_description_text_view);
        closeDialogButton = (ImageButton) view.findViewById(R.id.poi_close_dialog_button);
        closeDialogButton.setOnClickListener(v -> {
            // dismisses the current dialog view
            dismiss();
        });
        editPoiButton = (ImageButton) view.findViewById(R.id.poi_edit_button);
        editPoiButton.setOnClickListener(v -> {
            // todo: add action
            Toast.makeText(context, R.string.msg_no_action_defined, Toast.LENGTH_LONG).show();
        });
        deletePoiButton = (ImageButton) view.findViewById(R.id.poi_delete_button);
        deletePoiButton.setOnClickListener(v -> {
            // todo: add action
            Toast.makeText(context, R.string.msg_no_action_defined, Toast.LENGTH_LONG).show();
        });
    }

    private void loadPoiById(long id) {
        controller.getPoiById(id, event -> {
            switch (event.getType()) {
                case OK:
                    Poi poi = (Poi) event.getModel();
                    controller.getImages(poi, new FragmentHandler() {
                        @Override
                        public void onResponse(ControllerEvent controllerEvent) {
                            List<File> images = (List<File>) controllerEvent.getModel();
                            if (images.size() > 0) {
                                //TODO put them in some kind of swipe container
                                Picasso.with(context).load(images.get(0)).into(poiImage);
                            }
                        }
                    });

                    if (!poi.isPublic()) {
                        Picasso.with(context).load(R.drawable.image_msg_mode_private).fit().into(displayModeImage);
                    }

                    String[] typeValues = getResources().getStringArray(R.array.dialog_feedback_spinner_type);
                    typeTextView.setText(typeValues[(int) poi.getType()]);

                    titleTextView.setText(poi.getTitle());

                    dateTextView.setText(poi.getCreatedAt(Locale.GERMAN));
                    descriptionTextView.setText(poi.getDescription());

                    break;
                default:
                    //TODO was passiert wenn nicht gefunden..
                    //Careful getModel() will return null!
            }
        });
    }
}
