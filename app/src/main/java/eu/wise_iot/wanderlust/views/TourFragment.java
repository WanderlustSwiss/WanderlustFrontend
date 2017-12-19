package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.Old.Tour;

/**
 * TourController:
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class TourFragment extends Fragment {
    private static final String TAG = "TourOverviewFragment";
    private Tour tour;
    private Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();

        super.onCreate(savedInstanceState);
        //fetch data from database here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        TourController tc = new TourController();
        //fetch from local db
        UserTour data = tc.getDataView(Integer.parseInt(getArguments().get("routeID").toString()));

        View view = inflater.inflate(R.layout.fragment_tour, container, false);
        UserTour testTour = new UserTour(0, 0, "Tour1", "TourDescription", "picturePath", "polyline", 1, 1, true);

        TextView tvDistance = (TextView) view.findViewById(R.id.tourDistance);
        TextView tvAscend = (TextView) view.findViewById(R.id.tourAscend);
        TextView tvDescend = (TextView) view.findViewById(R.id.tourDescend);
        TextView tvDifficulty = (TextView) view.findViewById(R.id.tourDifficulty);
        TextView tvRegion = (TextView) view.findViewById(R.id.tourRegion);
        TextView tvRating = (TextView) view.findViewById(R.id.tourRating);
        TextView tvTitle = (TextView) view.findViewById(R.id.tourTitle);
        TextView tvTime = (TextView) view.findViewById(R.id.tourTime);
        ImageView ivTour = (ImageView) view.findViewById(R.id.tourImage);


        //set all information from local db
        //tvDistance.setText();

        //fetch from remote db
        tc.getDataViewServer(Integer.parseInt(getArguments().get("routeID").toString()), new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent event) {
                switch (event.getType()) {
                    case OK:
                        UserTour ut = (UserTour) event.getModel();
                        //get all needed information from server db
                        tvDescend.setText(ut.getTitle());
                        break;
                    default:
                        Toast.makeText(context, R.string.msg_e_server_error, Toast.LENGTH_LONG);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        TextView nameView = (TextView) view.findViewById(R.id.tour_title);
//        nameView.setText(tour.getName());
    }

}
