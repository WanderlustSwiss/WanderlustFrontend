package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import eu.wise_iot.wanderlust.R;

/**
 * DisclaimerFragment:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class DisclaimerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_disclaimer, container, false);
    }

    public static DisclaimerFragment newInstance() {
        Bundle args = new Bundle();
        DisclaimerFragment fragment = new DisclaimerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
