package eu.wise_iot.wanderlust;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.model.Tour;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.utils.MyJsonParser;
import eu.wise_iot.wanderlust.adapter.TourModelArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private Context context;
    private List<Tour> allToursTeaserList = new ArrayList<>();
    private MyJsonParser<Tour> parser;

    public static SearchFragment newInstance() {
        Bundle args = new Bundle();
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        parser = new MyJsonParser<>(Tour.class, context, R.raw.tours);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        populateTourTeaserList();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView listView = (ListView) view.findViewById(R.id.tourTeaserList);

        ArrayAdapter<Tour> adapter = new TourModelArrayAdapter(context, 0, allToursTeaserList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                String itemData = getTourData(position);
                bundle.putString(Constants.CLICKED_TOUR, itemData);

                Fragment fragment = new TourFragment();
                fragment.setArguments(bundle);

                FragmentManager framentManager = getFragmentManager();
                framentManager.beginTransaction().addToBackStack(fragment.toString()).replace(R.id.content_frame, fragment).commit();
            }
        });
    }

    private void populateTourTeaserList() {
        List<Tour> tourList = parser.getListFromResourceFile(R.raw.tours);
        for (Tour model : tourList) {
            allToursTeaserList.add(model);
        }
    }

    private String getTourData(int position) {
        Tour tour = allToursTeaserList.get(position);
        String jsonString = parser.getJsonStringFromObject(tour);
        return jsonString;
    }
}
