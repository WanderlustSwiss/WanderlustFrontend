package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ToursController;
import eu.wise_iot.wanderlust.models.Old.Tour;
import eu.wise_iot.wanderlust.views.adapters.TourModelArrayAdapter;
/**
 * ToursFragment:
 * represents the tours UI
 * @author Alexander Weinbeck
 * @license MIT
 */
//public class ToursFragment extends Fragment {
//
//    private Context context;
//    private ToursController controller;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        context = getActivity().getApplicationContext();
//        controller = new ToursController();
//    }
//
//    @Override
//    public void onViewCreated(View rootView, Bundle savedInstanceState) {
//        super.onViewCreated(rootView, savedInstanceState);
//        controller.getAllTours();
//        Tour e = new Tour(,);
//        List<Tour> t = new ArrayList<>();
//        t.add(To)
//        ListView myListView = (ListView) rootView.findViewById(R.id.ListSearchToursList);
//        TourModelArrayAdapter adapter = new TourModelArrayAdapter(getActivity(), R.layout.row_tours_search, );
//        myListView.setAdapter(adapter);
//    }
//
//}


