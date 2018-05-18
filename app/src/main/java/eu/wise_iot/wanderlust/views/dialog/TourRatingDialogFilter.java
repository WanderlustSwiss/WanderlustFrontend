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
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FilterController;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;

/**
 * TourRatingDialogFilter:
 *
 * @author Alexander Weinbeck
 * @license MIT
 */

public class TourRatingDialogFilter extends DialogFragment {

    private static FilterController controller;
    private static Tour tour;
    private static RatingBar ratingBar;
    private static TextView tourRatingInNumbers;

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
    private EditText titleEditText;
    private TextInputLayout titleTextLayout;
    private EditText descriptionEditText;
    private ImageButton buttonSave;
    private ImageButton buttonCancel;

    private int countRatedStars = 0;

    public static TourRatingDialogFilter newInstance(FilterController filterController,
                                                     RatingBar paramRatingBar) {
        TourRatingDialogFilter fragment = new TourRatingDialogFilter();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        ratingBar = paramRatingBar;
        controller = filterController;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rate_tour_filter, container, false);

        //titleEditText = (EditText) view.findViewById(R.id.rate_description);
        //titleTextLayout = (TextInputLayout) view.findViewById(R.id.rate_title_layout);
        firstStarButton = (ImageButton) view.findViewById(R.id.first_star_button);
        secondStarButton = (ImageButton) view.findViewById(R.id.second_star_button);
        thirdStarButton = (ImageButton) view.findViewById(R.id.third_star_button);
        fourthStarButton = (ImageButton) view.findViewById(R.id.fourth_star_button);
        fifthStarButton = (ImageButton) view.findViewById(R.id.fifth_star_button);
        buttonCancel = (ImageButton) view.findViewById(R.id.dialog_cancel_rate_filter_button);
        buttonSave = (ImageButton) view.findViewById(R.id.dialog_save_rate_filter_button);

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
        buttonSave.setOnClickListener(v -> {
            ratingBar.setRating(countRatedStars);
            getDialog().dismiss();
        });
        buttonCancel.setOnClickListener(v -> getDialog().dismiss());

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

