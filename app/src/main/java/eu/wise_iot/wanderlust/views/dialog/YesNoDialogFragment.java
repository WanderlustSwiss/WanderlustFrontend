package eu.wise_iot.wanderlust.views.dialog;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;

/**
 * YesNoDialogFragment: Fragment takes a short String message and displays a abort and confirm button
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class YesNoDialogFragment extends DialogFragment {
    private static final String MESSAGE = "message";
    private String message;

    private TextView messageTextView;
    private ImageButton abortButton;
    private ImageButton confirmButton;

    /**
     * @param message String: that gets displayed on the dialog
     * @return
     */
    public static YesNoDialogFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        YesNoDialogFragment fragment = new YesNoDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);

        Bundle args = getArguments();
        message = args.getString(MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_yes_no, container, false);
        messageTextView = (TextView) view.findViewById(R.id.message_text_view);
        abortButton = (ImageButton) view.findViewById(R.id.abort_button);
        confirmButton = (ImageButton) view.findViewById(R.id.confirm_button);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageTextView.setText(message);
        abortButton.setOnClickListener(v -> {
            dismiss();
        });
        confirmButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "confirm", Toast.LENGTH_SHORT).show();
        });
    }
}
