package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.wise_iot.wanderlust.R;

/**
 * Serves as placeholder until map is loaded
 *
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class BackgroundFragment extends Fragment {

    public static BackgroundFragment newInstance() {
        Bundle args = new Bundle();
        BackgroundFragment fragment = new BackgroundFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background, container, false);
    }

}
