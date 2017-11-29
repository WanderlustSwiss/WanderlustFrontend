package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;

/**
 * ProfileFragment:
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileMyToursFragment extends Fragment {

    private ListView listView;


    public ProfileMyToursFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileMyToursFragment.
     */

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
        View view = inflater.inflate(R.layout.fragment_profile_my_tours, container, false);
        listView = (ListView) view.findViewById(R.id.tourList);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


}
