package eu.wise_iot.wanderlust.views;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserGuideFragment extends Fragment {


    public UserGuideFragment() {
        // Required empty public constructor
    }

    public static UserGuideFragment newInstance() {

        Bundle args = new Bundle();

        UserGuideFragment fragment = new UserGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_user_guide, container, false);

        Button goToMapButton = (Button) rootView.findViewById(R.id.btn_go_to_map);
        goToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapFragment mapFragment = MapFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

}
