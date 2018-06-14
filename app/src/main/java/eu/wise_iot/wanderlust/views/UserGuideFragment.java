package eu.wise_iot.wanderlust.views;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;

/**
 * Fragment that contains a manual / user guide. Explains the most important features of the app.
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class UserGuideFragment extends Fragment {

    private SharedPreferences preferences;
    private boolean firstTimeOpened;

    private Button goToMapButton;
    private CheckBox disclaimerAccepted;
    private TextView readDisclaimerLink;



    public static UserGuideFragment newInstance() {
        Bundle args = new Bundle();
        UserGuideFragment fragment = new UserGuideFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        firstTimeOpened = preferences.getBoolean("firstTimeOpened", true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_user_guide, container, false);

        goToMapButton = rootView.findViewById(R.id.btn_go_to_map);
        disclaimerAccepted = rootView.findViewById(R.id.disclaimer_accepted_check_box);
        readDisclaimerLink = rootView.findViewById(R.id.read_disclaimer_link);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View container = view.findViewById(R.id.disclaimer_accept_container);

        if (firstTimeOpened) {
            goToMapButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.heading_icon_unselected));

            disclaimerAccepted.setOnCheckedChangeListener((compoundButton, b) -> {
                if (disclaimerAccepted.isChecked()) {
                    goToMapButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.primary_main));
                } else {
                    goToMapButton.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.heading_icon_unselected));
                }
            });
        } else {
            container.setVisibility(View.GONE);
        }

        goToMapButton.setOnClickListener(v -> {
            if (disclaimerAccepted.isChecked() || container.getVisibility() == View.GONE) {
                Fragment mapFragment = getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
                if (mapFragment == null) mapFragment = MapFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                        .commit();

                // save that app has been opened
                preferences.edit().putBoolean("firstTimeOpened", false).apply();

                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            } else {
                Toast.makeText(getActivity(), R.string.msg_accept_disclaimer, Toast.LENGTH_LONG).show();
            }
        });

        readDisclaimerLink.setOnClickListener(v -> {
            Fragment disclaimerFragment = getFragmentManager().findFragmentByTag(Constants.DISCLAIMER_FRAGMENT);
            if (disclaimerFragment == null)
                disclaimerFragment = DisclaimerFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, disclaimerFragment, Constants.DISCLAIMER_FRAGMENT)
                    .addToBackStack(Constants.DISCLAIMER_FRAGMENT)
                    .commit();
        });
    }
}
