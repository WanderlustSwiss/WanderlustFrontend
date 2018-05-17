package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.wise_iot.wanderlust.R;

/**
 * Processingfragment indicates a process before loading a fragment
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ProcessingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_processing, container, false);
    }

    public static ProcessingFragment newInstance() {
        Bundle args = new Bundle();
        ProcessingFragment fragment = new ProcessingFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
