package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
import eu.wise_iot.wanderlust.controllers.TourController;
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
    private Context context;
    private ArrayList<GeoPoint> trackedTour;

    private Tour tour;
    private EditText titleEditText;
    private TextInputLayout titleTextLayout;
    private EditText descriptionEditText;
    private Spinner difficultySpinner;
    private Spinner publicSpinner;

    private ImageButton buttonSave;
    private ImageButton buttonCancel;
    private TourController controller;
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
        context = getActivity();
        controller = new TourController(this.tour);

        Bundle args = getArguments();
        isNewTour = args.getBoolean(Constants.CREATE_TOUR_IS_NEW);
        if (isNewTour) {
            trackedTour = (ArrayList<GeoPoint>) args.getSerializable(Constants.CREATE_TOUR_TRACK);
            String polyline = PolyLineEncoder.encode(trackedTour, 10);
            this.tour.setPolyline(polyline);

        }

        // Todo: implement season selector
        ArrayList<String> seasons = new ArrayList<>();
        seasons.add("summer");
        seasons.add("spring");
        this.tour.setSeasons(seasons);

        // ToDo: Make Region spinner
        tour.setRegion(2);

        // set style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);

        createTourhandler = controllerEvent -> {
            if (controllerEvent.getType() == EventType.OK) {
                Toast.makeText(getActivity(), "Erfolg", Toast.LENGTH_SHORT).show();
                dismiss();
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

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();
        if (!isNewTour) {
            fillInDataFromExistingTour();
        }
    }

    /**
     * initializes the actions of all the image buttons (save and cancel) of the fragment
     */
    private void initActionControls() {
        buttonSave.setOnClickListener(v -> {
            if (titleEditText != null && titleEditText.getText() != null) {
                String title = titleEditText.getText().toString();
                tour.setTitle(title);
            }

            if (tour.getTitle().isEmpty()) {
                titleTextLayout.setError(getString(R.string.message_add_title));
                return;
            }

            if (descriptionEditText != null && descriptionEditText.getText() != null) {
                String description = descriptionEditText.getText().toString();
                tour.setDescription(description);
            }

            if (difficultySpinner != null) {
                tour.setDifficulty(difficultySpinner.getSelectedItemPosition() + 1);
            }


            if (publicSpinner != null) {
                publish = !tour.isPublic() && publicSpinner.getSelectedItemPosition() == 0;
                tour.setPublic( publicSpinner.getSelectedItemPosition() == 0);
            }


            if (isNewTour) {
                controller.createTour(createTourhandler);
            } else {
                // Todo: Edit the selected tour
            }
        });

        buttonCancel.setOnClickListener(view -> dismiss());
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
}
