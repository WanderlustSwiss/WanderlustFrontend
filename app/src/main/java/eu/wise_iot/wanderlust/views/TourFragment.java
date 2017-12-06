package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.Old.Tour;
import eu.wise_iot.wanderlust.views.adapters.MyRecyclerViewAdapter;

/**
 * TourOverviewFragment:
 * @author Fabian Schwander
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

        View view = inflater.inflate(R.layout.fragment_tourslist, container, false);

        return inflater.inflate(R.layout.fragment_tour, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        TextView nameView = (TextView) view.findViewById(R.id.tour_title);
//        nameView.setText(tour.getName());
    }

}
