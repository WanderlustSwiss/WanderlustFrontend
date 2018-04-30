package eu.wise_iot.wanderlust.views;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;

/**
 * Fragment that contains a manual / user guide. Explains the most important features of the app.
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class UserGuideFragment extends Fragment {

    private Button goToMapButton;

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

        goToMapButton = (Button) rootView.findViewById(R.id.btn_go_to_map);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        goToMapButton.setOnClickListener(v -> {

            Fragment mapFragment = getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
            if (mapFragment == null) mapFragment = MapFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                    .addToBackStack(null)
                    .commit();
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        });
    }
}
