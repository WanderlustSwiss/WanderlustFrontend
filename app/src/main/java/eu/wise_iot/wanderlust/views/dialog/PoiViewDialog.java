package eu.wise_iot.wanderlust.views.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.GeoObject;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;

/**
 * PoiViewDialog:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class PoiViewDialog extends DialogFragment {
    private static final String TAG = "PoiViewDialog";
    //Assume there is only 1 PoiViewDialog
    private static Poi currentPoi;
    private Activity context;
    private ImageView poiImage;
    private TextView typeTextView;
    private TextView titleTextView;
    private TextView dateTextView;
    private TextView descriptionTextView;
    private ImageButton closeDialogButton;
    private ImageButton editPoiButton;
    private ImageButton deletePoiButton;
    private ImageButton sharePoiButton;
    private ImageButton privateModeButton;

    private PoiController controller;
    private TextView occupationTitleSac;
    private TableLayout sacOccupation;
    private Map<String, Integer> monthIds = new HashMap<>();


    /**
     * Create a PoiViewDialog from a Poi object
     *
     * @param poi
     */
    public static PoiViewDialog newInstance(Poi poi) {
        PoiViewDialog dialog = new PoiViewDialog();
        currentPoi = poi;
        dialog.setStyle(R.style.my_no_border_dialog_theme, R.style.AppTheme);
        long poiId = poi.getPoi_id();
        Bundle args = new Bundle();
        args.putLong(Constants.POI_ID, poiId);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * Create a PoiViewDialog from a GeoObject
     *
     * @param geoObject The GeoObject which is shown in the Poi dialog
     */
    public static PoiViewDialog newInstance(GeoObject geoObject, long geoObjectTypeId) {
        PoiViewDialog dialog = new PoiViewDialog();
        ArrayList<ImageInfo> list = new ArrayList<>();
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setPath(geoObject.getImageLink());
        list.add(imageInfo);

        currentPoi = new Poi((long) geoObjectTypeId, geoObject.getTitle(), geoObject.getDescription(), geoObject.getLongitude(), geoObject.getLatitude(), (float) geoObject.getElevation(), -1, (long) -1, (int) geoObjectTypeId, true, list, "", "");
        dialog.setStyle(R.style.my_no_border_dialog_theme, R.style.AppTheme);
        Bundle args = new Bundle();
        args.putLong(Constants.POI_ID, geoObjectTypeId);
        dialog.setArguments(args);
        return dialog;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        controller = new PoiController();

        Bundle args = getArguments();
        long poiId = args.getLong(Constants.POI_ID);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_poi_view, container, false);
        poiImage = (ImageView) view.findViewById(R.id.poi_image);
        typeTextView = (TextView) view.findViewById(R.id.poi_type_text_view);
        titleTextView = (TextView) view.findViewById(R.id.poi_title_text_view);
        dateTextView = (TextView) view.findViewById(R.id.poi_date_text_view);
        descriptionTextView = (TextView) view.findViewById(R.id.poi_description_text_view);
        closeDialogButton = (ImageButton) view.findViewById(R.id.poi_close_dialog_button);
        editPoiButton = (ImageButton) view.findViewById(R.id.poi_edit_button);
        deletePoiButton = (ImageButton) view.findViewById(R.id.poi_delete_button);
        sharePoiButton = (ImageButton) view.findViewById(R.id.poi_share_button);
        sacOccupation = (TableLayout) view.findViewById(R.id.tableLayout_occupation_sac);
        occupationTitleSac = (TextView) view.findViewById(R.id.title_occupation);
        privateModeButton = (ImageButton) view.findViewById(R.id.poi_mode_private_image);
        // default not visible
        sacOccupation.setVisibility(View.GONE);
        occupationTitleSac.setVisibility(View.GONE);

        if (controller.isOwnerOf(currentPoi)) {
            this.showControlsForOwner();
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();
        fillOutPoiView(view);
    }

    private void initActionControls() {

        sharePoiButton.setOnClickListener(v -> shareImage());

        closeDialogButton.setOnClickListener(v -> {
            // dismisses the current dialog view
            dismiss();
        });

        editPoiButton.setOnClickListener(v -> {
            if (controller.isOwnerOf(currentPoi)) {
                PoiEditDialog dialog = PoiEditDialog.newInstance(currentPoi);
                dialog.show(getFragmentManager(), Constants.EDIT_POI_DIALOG);
            }
        });

        deletePoiButton.setOnClickListener(v -> {
            if (controller.isOwnerOf(currentPoi)) {
                ConfirmDeletePoiDialog dialog = ConfirmDeletePoiDialog.newInstance(context, controller, currentPoi, getString(R.string.message_confirm_delete_poi));
                dialog.show(getFragmentManager(), Constants.CONFIRM_DELETE_POI_DIALOG);
                dismiss();
            }
        });

        privateModeButton.setOnClickListener(v -> Toast.makeText(context, getString(R.string.poi_mode_is_private), Toast.LENGTH_LONG).show());
    }

    private void fillOutPoiView(View view) {
        if (currentPoi.getType() >= 0) {

            controller.getImages(currentPoi, controllerEvent -> {
                List<File> images = (List<File>) controllerEvent.getModel();
                if (images.size() > 0) {
                    Picasso.with(context).load(images.get(0)).fit().placeholder(R.drawable.progress_animation).into(poiImage);
                }
            });
        } else {
            List<File> images = new ArrayList<>();
            images.add(new File(currentPoi.getImagePaths().get(0).getPath()));
            Picasso.with(context).load(images.get(0).getPath()).fit().centerCrop().into(poiImage);
        }

        if (currentPoi.isPublic()) privateModeButton.setVisibility(View.GONE);

        String[] typeValues = getResources().getStringArray(R.array.dialog_feedback_spinner_type);
        String[] geoObjectTypeValues = getResources().getStringArray(R.array.dialog_feedback_spinner_geoobject_type);


        String poiType;

        if(currentPoi.getType() >= 0){
            poiType = typeValues[(int) currentPoi.getType()];
        } else {
            poiType = geoObjectTypeValues[(-1 * (int) currentPoi.getType()) -1];
            sharePoiButton.setVisibility(View.GONE);
        }
        String elevationText = String.format("%.0f  %s", currentPoi.getElevation(), getString(R.string.meter_above_sea_level_abbreviation));
        String typeText = String.format("%s (%s)", poiType, elevationText);
        typeTextView.setText(typeText);

        titleTextView.setText(currentPoi.getTitle());

        // only show date text view if poi is a sac hut
        if(currentPoi.getType() >= 0){
            dateTextView.setText(currentPoi.getCreatedAt(Locale.GERMAN));
        } else {
            dateTextView.setVisibility(View.GONE);
        }

        // TODO: can be removed after separating sac from poi view
        if (currentPoi.getType() == Constants.TYPE_SAC) {
            String description = showSacOccupation(currentPoi.getDescription(), view);
            currentPoi.setDescription(description);
        }

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
        PoiViewDialog.currentPoi = currentPoi;
    }

    public PoiController getController() {
        return controller;
    }

    public void setController(PoiController controller) {
        this.controller = controller;
    }

    private void shareImage(){
        File image = controller.getImageToShare(currentPoi);
        String title;
        if (image != null){
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("image/jpg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image));
            if (currentPoi.getElevation() != Integer.MAX_VALUE) {
                title = currentPoi.getTitle() + ", " +
                        String.format("%.0f  %s",
                                currentPoi.getElevation()
                                , getString(R.string.meter_above_sea_level_abbreviation));
            }else{
                title = currentPoi.getTitle();
            }
            String description = currentPoi.getDescription() + " @wanderlust-app";
            shareIntent.putExtra(Intent.EXTRA_TEXT, description);
            shareIntent.putExtra(Intent.EXTRA_TITLE, title);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title_poi)));
        }
    }

    /**
     * Displays to occupation table for SAC Pois
     *
     * @param text the description containing the occupation information
     *
     */
    private String showSacOccupation(String text, View view){
        initMonthTableString();

        sacOccupation.setVisibility(View.VISIBLE);
        occupationTitleSac.setVisibility(View.VISIBLE);


        StringBuilder editedDescription = new StringBuilder();
        Resources res = getResources();

        String[] textSplit = text.split("\n");
        for (String aTextSplit : textSplit) {
            if (aTextSplit.contains("Teilweise bewartet: ")) {
                String occupiedMonthString = aTextSplit.substring(20);
                String[] occupiedMonth = occupiedMonthString.split(",");
                for (String anOccupiedMonth : occupiedMonth) {
                    String month = anOccupiedMonth.toLowerCase();
                    int id = monthIds.get(month);
                    TextView currentMonthRow = (TextView) view.findViewById(id);
                    if (currentMonthRow != null) {
                        currentMonthRow.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.medium));
                    }
                }
            } else if (aTextSplit.contains("Bewartet: ")) {

                String occupiedMonthString = aTextSplit.substring(10);
                String[] occupiedMonth = occupiedMonthString.split(",");
                for (String anOccupiedMonth : occupiedMonth) {
                    String month = anOccupiedMonth.toLowerCase();
                    int id = monthIds.get(month);
                    TextView currentMonthRow = (TextView) view.findViewById(id);
                    if (currentMonthRow != null) {
                        currentMonthRow.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.success_easy));
                    }
                }
            } else {
                editedDescription.append(aTextSplit).append("\n");
            }
        }
        int lasIndex = editedDescription.lastIndexOf("\n");
        return editedDescription.substring(0, lasIndex);
    }



    private void initMonthTableString() {
        monthIds.put("jan", R.id.occupation_jan);
        monthIds.put("feb", R.id.occupation_feb);
        monthIds.put("mär", R.id.occupation_mär);
        monthIds.put("apr", R.id.occupation_apr);
        monthIds.put("mai", R.id.occupation_mai);
        monthIds.put("jun", R.id.occupation_jun);
        monthIds.put("jul", R.id.occupation_jul);
        monthIds.put("aug", R.id.occupation_aug);
        monthIds.put("sep", R.id.occupation_sep);
        monthIds.put("okt", R.id.occupation_okt);
        monthIds.put("nov", R.id.occupation_nov);
        monthIds.put("dez", R.id.occupation_dez);
    }

}
