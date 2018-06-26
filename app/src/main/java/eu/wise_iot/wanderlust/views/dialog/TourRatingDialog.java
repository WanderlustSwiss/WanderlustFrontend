package eu.wise_iot.wanderlust.views.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourRate;
import eu.wise_iot.wanderlust.views.TourFragment;

/**
 * TourRatingDialog:
 *
 * @author Rilind Gashi
 * @license GPL-3.0
 */

public
class TourRatingDialog extends DialogFragment {

    private static TourController controller;
    private static Tour tour;

    private static RatingBar ratingBar;
    private static TextView tourRatingInNumbers;
    private static TourFragment tourFragment;

    private final ImageButton[] starButtonCollection = new ImageButton[5];

    private ImageButton firstStarButton;
    private ImageButton secondStarButton;
    private ImageButton thirdStarButton;
    private ImageButton fourthStarButton;
    private ImageButton fifthStarButton;


    private static final String TAG = "TourRatingDialog";
    private FragmentHandler ratingHandler;
    private Context context;
    private Rating rating;
    private EditText descriptionEditText;
    private ImageButton buttonSave;
    private ImageButton buttonCancel;

    private int countRatedStars = 0;

    public static TourRatingDialog newInstance(Tour paramTour, TourController tourController,
                                               TourFragment paramTourFragment) {
        TourRatingDialog fragment = new TourRatingDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        tour = paramTour;
        tourFragment = paramTourFragment;
        controller = tourController;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rate_tour, container, false);

        firstStarButton = view.findViewById(R.id.first_star_button);
        secondStarButton = view.findViewById(R.id.second_star_button);
        thirdStarButton = view.findViewById(R.id.third_star_button);
        fourthStarButton = view.findViewById(R.id.fourth_star_button);
        fifthStarButton = view.findViewById(R.id.fifth_star_button);
        buttonCancel = view.findViewById(R.id.dialog_canel_rate_button);
        buttonSave = view.findViewById(R.id.rate_save_button);

        starButtonCollection[0] = firstStarButton;
        starButtonCollection[1] = secondStarButton;
        starButtonCollection[2] = thirdStarButton;
        starButtonCollection[3] = fourthStarButton;
        starButtonCollection[4] = fifthStarButton;

        return view;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    private void setupListeners(){
        long rate = controller.alreadyRated(tour.getTour_id());
        if(rate == 0) {
            buttonSave.setOnClickListener(v -> controller.setRating(tour, countRatedStars, controllerEvent0 -> {
                getDialog().dismiss();
                controller.getTourRating(controllerEvent1 -> {
                    switch (controllerEvent1.getType()) {
                        case OK:
                            TourRate tourRate = (TourRate) controllerEvent1.getModel();
                            tourFragment.updateRating(tourRate);
                    }
                });
            }));
        }
        else{
            for(int i=0; i < rate; i++){
                starButtonCollection[i].setImageResource(R.drawable.ic_rate_star_yellow_32dp);
            }
            buttonSave.setImageResource(R.drawable.ic_check_disabled_24dp);
        }
        buttonCancel.setOnClickListener(v -> {
            // dismisses the current dialog view
            getDialog().dismiss();

        });
        for(int i=0; i < starButtonCollection.length; i++){
            final int x = i+1;
            starButtonCollection[i].setOnClickListener(v -> changeButtonColor(x));
        }
    }

    private void changeButtonColor(int selectedStar){
        countRatedStars = selectedStar;
        for(int i=0; i < selectedStar; i++){
            starButtonCollection[i].setImageResource(R.drawable.ic_rate_star_yellow_32dp);
        }
        for(int i=starButtonCollection.length; i > selectedStar; i--){
            starButtonCollection[i-1].setImageResource(R.drawable.ic_rate_star_transparent_32dp);
        }
    }
}

