package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import eu.wise_iot.wanderlust.R;

/**
 * ProfileFragment:
 * @author Baris Demirci
 * @license MIT
 */

public class ProfilePOIFragment extends Fragment {

    private ListView listView;


    public ProfilePOIFragment() {
        // Required empty public constructor
    }


    public static ProfileMyToursFragment newInstance(String param1, String param2) {
        ProfileMyToursFragment fragment = new ProfileMyToursFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_poi, container, false);
        listView = (ListView) view.findViewById(R.id.tourList);


        return view;
    }
}
