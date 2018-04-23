package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.AddressPoint;
import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;

/**
 * CreateTour:
 *
 * @author Joshua Meier
 * @license MIT
 */
public class CreateTourDialog extends DialogFragment {
    private static final String TAG = "CreateTourDialog";
    private FragmentHandler<Trip> createTourhandler;
    private ArrayList<GeoPoint> trackedTour;
    private List<Region> regions;
    private List<String> regionNames;

    private Tour tour;
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


    private ImageButton buttonSave;
    private ImageButton buttonCancel;
    private TourController tourController;
    private MapController mapController;
    private boolean isNewTour;
    private boolean publish;

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
        tourController = new TourController(this.tour);
        mapController = new MapController(this);
        regions = tourController.getAllRegions();
        regionNames = new ArrayList<>();

        for (Region region : regions) {
            regionNames.add(region.getName());
        }

        Bundle args = getArguments();
        isNewTour = args.getBoolean(Constants.CREATE_TOUR_IS_NEW);
        if (isNewTour) {
            trackedTour = (ArrayList<GeoPoint>) args.getSerializable(Constants.CREATE_TOUR_TRACK);
            String polyline = PolyLineEncoder.encode(trackedTour, 10);
            this.tour.setPolyline(polyline);

        }

        // set style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

        createTourhandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                Toast.makeText(getActivity(), R.string.create_tour_saved, Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getActivity(), R.string.connection_fail, Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_create_tour, container, false);

        titleEditText = (EditText) view.findViewById(R.id.tour_title);
        titleTextLayout = (TextInputLayout) view.findViewById(R.id.tour_title_layout);
        descriptionEditText = (EditText) view.findViewById(R.id.tour_description);
        publicSpinner = (Spinner) view.findViewById(R.id.tour_is_public);
        difficultySpinner = (Spinner) view.findViewById(R.id.tour_difficulty);
        buttonSave = (ImageButton) view.findViewById(R.id.tour_save_button);
        buttonCancel = (ImageButton) view.findViewById(R.id.tour_return_button);
        regionSpinner = (Spinner) view.findViewById(R.id.tour_region);
        winterCheckBox = (CheckBox) view.findViewById(R.id.create_tour_checkbox_winter);
        summerCheckBox = (CheckBox) view.findViewById(R.id.create_tour_checkbox_summer);
        fallCheckBox = (CheckBox) view.findViewById(R.id.create_tour_checkbox_fall);
        springCheckBox = (CheckBox) view.findViewById(R.id.create_tour_checkbox_spring);


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, regionNames);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(spinnerArrayAdapter);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();
        fillInDataFromFirstGeopoint();
        if (!isNewTour) {
            fillInDataFromExistingTour();
        }
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
                // Todo: Edit the selected tour
            }
        });

        buttonCancel.setOnClickListener(view -> {
            if (isNewTour) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.create_tour_not_saved)
                        .setMessage(R.string.create_tour_not_save_tour)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, positiveButton) -> {
                            dismiss();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            } else {
                dismiss();
            }

        });
    }

    /**
     * Fills all data from a existing Tour into the form for the user
     */
    private void fillInDataFromExistingTour() {
        titleEditText.setText(this.tour.getTitle());
        descriptionEditText.setText(this.tour.getDescription());
        difficultySpinner.setSelection((int) this.tour.getDifficulty() - 1);
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
}
