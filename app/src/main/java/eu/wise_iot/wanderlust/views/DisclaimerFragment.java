package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

import eu.wise_iot.wanderlust.R;

/**
 * Serves as disclaimer for the whole application
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class DisclaimerFragment extends Fragment {

    private TextView authorsTextView;

    public static DisclaimerFragment newInstance() {
        Bundle args = new Bundle();
        DisclaimerFragment fragment = new DisclaimerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_disclaimer, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        authorsTextView = (TextView) view.findViewById(R.id.authors_text_view);

        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        String authorsText = getActivity().getResources().getString(R.string.disclaimer_copy_wright);
        authorsText += year + " ";
        authorsText += getActivity().getResources().getString(R.string.disclaimer_authors);
        authorsTextView.setText(authorsText);
    }
}
