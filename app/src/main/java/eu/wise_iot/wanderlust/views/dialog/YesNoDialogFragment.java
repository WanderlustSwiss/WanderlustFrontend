package eu.wise_iot.wanderlust.views.dialog;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import eu.wise_iot.wanderlust.R;

/**
 * YesNoDialogFragment: Fragment takes a short String message and displays a abort and confirm button
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class YesNoDialogFragment extends DialogFragment {

    public static YesNoDialogFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString("message", message);
        YesNoDialogFragment fragment = new YesNoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_yes_no, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
}
