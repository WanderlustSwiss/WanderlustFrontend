package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.Old.Tour;

/**
 * TourController:
 *
 * @author Alexander Weinbeck, Rilind Gashi
 * @license MIT
 */
public class TourFragment extends Fragment {
    private static final String TAG = "TourOverviewFragment";
    private Tour tour;
    private static TourController tourController;
    private Context context;
    private static UserTour userTour;
    private static UserTour userTourWithGeoData;
    private static Polyline polyline;

    private ImageView imageViewTourImage;
    private ImageButton favButton;
    private TextView tourRegion;
    private TextView tourTitle;
    private ImageButton tourSavedButton;
    private ImageButton tourSharedButton;
    private TextView textViewTourDistance;
    private TextView textViewAscend;
    private TextView textViewDuration;
    private TextView textViewDescend;
    private TextView textViewDifficulty;
    private TextView textViewDescription;
    private Button jumpToStartLocationButton;
    private static MapFragment mapFragment;

    private Favorite favorite;
    private int[] highProfile;

    public TourFragment() {
        // Required empty public constructor
    }


    /**
     * Static instance constructor.
     *
     * @return Fragment: TourFragment
     */
    public static TourFragment newInstance(UserTour paramUserTour) {

        Bundle args = new Bundle();
        TourFragment fragment = new TourFragment();
        tourController = new TourController();
        mapFragment = new MapFragment();
        fragment.setArguments(args);
        userTour = paramUserTour;
        //Hello, its me
        tourController.getSelectedUserTour(userTour, new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                userTourWithGeoData = (UserTour) controllerEvent.getModel();
                userTour.setPolyline(userTourWithGeoData.getPolyline());
                userTour.setElevation(userTourWithGeoData.getElevation());
            }
        });
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tour, container, false);
        setupTourView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public void setupTourView(View view) {
        imageViewTourImage = (ImageView) view.findViewById(R.id.tourImage);
        favButton = (ImageButton) view.findViewById(R.id.favButton);

        tourRegion = (TextView) view.findViewById(R.id.tourRegion);
        tourTitle = (TextView) view.findViewById(R.id.tourTitle);
        tourSavedButton = (ImageButton) view.findViewById(R.id.tourSaved);
        tourSharedButton = (ImageButton) view.findViewById(R.id.tourShared);
        textViewTourDistance = (TextView) view.findViewById(R.id.tourDistance);
        textViewAscend = (TextView) view.findViewById(R.id.tourAscend);
        textViewDuration = (TextView) view.findViewById(R.id.tourDuration);
        textViewDescend = (TextView) view.findViewById(R.id.tourDescend);
        textViewDifficulty = (TextView) view.findViewById(R.id.tourDifficulty);
        textViewDescription = (TextView) view.findViewById(R.id.tourDescription);
        jumpToStartLocationButton = (Button) view.findViewById(R.id.jumpToStartLocationButton);

        long difficulty = userTour.getDifficulty();
        if (difficulty >= 6)
            textViewDifficulty.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.t6), null, null, null);
        else if (difficulty >= 4)
            textViewDifficulty.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.t4_t5), null, null, null);
        else if (difficulty >= 2)
            textViewDifficulty.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.t2_t3), null, null, null);
        else
            textViewDifficulty.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.t1), null, null, null);

        fillUiElements();

    }

    public void fillUiElements() {

        File tourImage = userTour.getImageById((byte) 0);
        long fileSize = tourImage.length();
        if (fileSize != 0) {
            Picasso.with(context)
                    .load(tourImage)
                    .into(this.imageViewTourImage);
        } else {
            Picasso.with(context)
                    .load(R.drawable.no_image_found)
                    .into(this.imageViewTourImage);
        }

        if (tourController.isFavorite(userTour.getTour_id())) {
            favButton.setImageResource(R.drawable.ic_favorite_red_24dp);
//            favButton.setOnClickListener((View v) -> unfavoriteTour());
        } else {
            favButton.setImageResource(R.drawable.ic_favorite_white_24dp);
//            favButton.setOnClickListener((View v) -> favoriteTour());
        }

        try {
            highProfile = tourController.getHighProfile(userTour.getElevation());
        } catch (IOException e) {
            e.printStackTrace();
        }
        tourRegion.setText("");
        tourTitle.setText(userTour.getTitle());
        textViewTourDistance.setText(tourController.convertToStringDistance(userTour.getDistance()));
        textViewAscend.setText(String.valueOf(userTour.getAscent()) + " m");
        textViewDescend.setText(String.valueOf(userTour.getDescent()) + " m");
        textViewDuration.setText(tourController.convertToStringDuration(userTour.getDuration()));
        textViewDescription.setText(userTour.getDescription());

        textViewDifficulty.setText("T" + String.valueOf(userTour.getDifficulty()));
        jumpToStartLocationButton.setOnClickListener((View v) -> showMapWithTour());
    }

    public void favoriteTour() {
        tourController.setFavorite(userTour.getTour_id(), new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                favButton.setImageResource(R.drawable.ic_favorite_red_24dp);
            }
        });
    }

//    public void unfavoriteTour(){
//        tourController.deleteFavorite(userTour.getTour_id(), new FragmentHandler() {
//            @Override
//            public void onResponse(ControllerEvent controllerEvent) {
//                favButton.setImageResource(R.drawable.ic_favorite_white_24dp);
//            }
//        });
//    }

    public void showMapWithTour() {
        ArrayList<GeoPoint> polyList = PolyLineEncoder.decode(userTour.getPolyline(), 10);
        Road road = new Road(polyList);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        MapFragment mapFragment = MapFragment.newInstance(roadOverlay);

        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                .commit();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

    }


}
