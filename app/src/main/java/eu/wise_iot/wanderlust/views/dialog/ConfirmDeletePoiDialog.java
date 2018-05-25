package eu.wise_iot.wanderlust.views.dialog;


import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.views.ProfileFragment;

/**
 * ConfirmDeletePoiDialog: Fragment takes a short String message and displays a abort and confirm button
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class ConfirmDeletePoiDialog extends DialogFragment {
    private static final String MESSAGE = "message";
    private String message;

    private Activity context;
    private PoiController controller;
    private Poi currentPoi;

    private TextView messageTextView;
    private ImageButton abortButton;
    private ImageButton confirmButton;

    //for profile list poi's, is set external by profile fragment
    private ProfileFragment profileFragment;

    /**
     * @param message String: that gets displayed on the dialog
     * @return
     */
    public static ConfirmDeletePoiDialog newInstance(Context context, PoiController controller, Poi currentPoi, String message) {
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        ConfirmDeletePoiDialog fragment = new ConfirmDeletePoiDialog();
        //fragment.context = context;
        fragment.controller = controller;
        fragment.currentPoi = currentPoi;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set style
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
        context = getActivity();
        Bundle args = getArguments();
        message = args.getString(MESSAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_delete_poi, container, false);

        messageTextView = view.findViewById(R.id.message_text_view);
        abortButton = view.findViewById(R.id.abort_button);
        confirmButton = view.findViewById(R.id.confirm_button);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageTextView.setText(message);

        abortButton.setOnClickListener(v -> dismiss());

        confirmButton.setOnClickListener(v -> {
            controller.deletePoi(currentPoi, e -> {
                EventType eventType = e.getType();
                switch (eventType) {
                    case OK:
                        Toast.makeText(context, R.string.poi_fragment_success_delete, Toast.LENGTH_LONG).show();


                        profileFragment = (ProfileFragment) context.getFragmentManager().findFragmentByTag(Constants.PROFILE_FRAGMENT);

                        if(profileFragment != null){
                            View vw = profileFragment.getView();
                            //profileFragment.setupPOIs(vw);
                            profileFragment.setProfileStats();
                        }

                        dismiss();
                        break;
                    default: // fail
                        Toast.makeText(context, R.string.poi_fragment_fail_delete, Toast.LENGTH_LONG).show();
                        break;
                }
            });
            dismiss();
        });
    }

    public void setupForProfileList(ProfileFragment profileFragment){
        this.profileFragment = profileFragment;
    }
}
