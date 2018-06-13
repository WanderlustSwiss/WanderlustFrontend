package eu.wise_iot.wanderlust.views;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.views.controls.LoadingDialog;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Represents the Reset Password section
 *
 * @author TODO ??
 * @license MIT
 */
public class StartupResetPasswordFragment extends Fragment {

    private Context context;
    private static final String TAG = "ResetPWFragment";

    private Button btnForgotPassword;
    private EditText editTextForgotPassword;
    private TextInputLayout textInputForgotPassword;
    private TextView redirectToLogin;

    private final LoginController loginController;

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,5}$", Pattern.CASE_INSENSITIVE);


    public StartupResetPasswordFragment() {
        loginController = new LoginController();
    }

    public static StartupResetPasswordFragment newInstance() {
        Bundle args = new Bundle();
        StartupResetPasswordFragment fragment = new StartupResetPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    private final FragmentHandler fragmentHandler = new FragmentHandler() {
//        @Override
//        public void onResponse(ControllerEvent event) {
//            EventType eventType = event.getType();
//            switch (eventType) {
//                case OK:
//                    Toast.makeText(context, R.string.forgot_password_reset_mail_success, Toast.LENGTH_LONG).show();
//                    Fragment startupLoginFragment = getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
//                    if (startupLoginFragment == null)startupLoginFragment = StartupLoginFragment.newInstance();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.content_frame, startupLoginFragment, Constants.LOGIN_FRAGMENT)
//                            .commit();
//
//                    break;
//                default:
//                    Toast.makeText(context, R.string.forgot_password_reset_mail_fail, Toast.LENGTH_LONG).show();
//                    break;
//            }
//        }
//    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startup_reset_password, container, false);
        btnForgotPassword = view.findViewById(R.id.btn_forgot_pw);
        editTextForgotPassword = view.findViewById(R.id.input_forgot_pw);
        textInputForgotPassword = view.findViewById(R.id.text_input_forgot_pw);
        redirectToLogin = view.findViewById(R.id.link_login);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();

        //handle keyboard closing
        view.findViewById(R.id.rootLayout).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            return true;
        });
    }

    private void initActionControls() {
        btnForgotPassword.setOnClickListener(v -> {
            String inputMail = editTextForgotPassword.getText().toString();
            if (!validateEmail(inputMail)) {
                textInputForgotPassword.setError(getString(R.string.registration_email_invalid));
            } else {
                LoadingDialog.getDialog().show(getActivity());
                loginController.resetPassword(inputMail, controllerEvent -> {
                    switch (controllerEvent.getType()) {
                        case OK:
                            Toast.makeText(context, R.string.forgot_password_reset_mail_success, Toast.LENGTH_LONG).show();
                            Fragment startupLoginFragment = getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
                            if (startupLoginFragment == null)
                                startupLoginFragment = StartupLoginFragment.newInstance();
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, startupLoginFragment, Constants.LOGIN_FRAGMENT)
                                    .commit();
                            break;
                        case NOT_FOUND:
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "ERROR: Server Response arrived -> Email was not found");
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.msg_email_not_found), Toast.LENGTH_LONG).show();
                            break;
                        case SERVER_ERROR:
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "ERROR: Server Response arrived -> SERVER ERROR");
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.msg_server_error), Toast.LENGTH_LONG).show();
                            break;
                        case NETWORK_ERROR:
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "ERROR: Server Response arrived -> NETWORK ERROR");
                            break;
                        default:
                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "ERROR: Server Response arrived -> UNDEFINED ERROR");
                            Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.forgot_password_reset_mail_fail), Toast.LENGTH_LONG).show();
                    }

                    LoadingDialog.getDialog().dismiss();
                });
            }

        });

        redirectToLogin.setOnClickListener(v -> {
            Fragment startupLoginFragment = getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
            if (startupLoginFragment == null)
                startupLoginFragment = StartupLoginFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, startupLoginFragment, Constants.LOGIN_FRAGMENT)
                    .commit();
        });
    }


    private boolean validateEmail(String mail) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
        return matcher.find();
    }
}

//    /**
//     * handles async backend request for performing a password reset
//     * this will keep the UI responsive
//     *
//     * @author Alexander Weinbeck
//     * @license MIT
//     */
//    private class AsyncResetPassword extends AsyncTask<Void, Void, Void> {
//        private final String email;
//        private ControllerEvent event;
//
//        AsyncResetPassword(String email){
//            this.email = email;
//        }
//        @Override
//        protected void onPreExecute() {
//            //this method will be running on UI thread
//            LoadingDialog.getDialog().show(getActivity());
//        }
//        @Override
//        protected Void doInBackground(Void... params) {
//            setThreadPriority(-10);
//            event = loginController.resetPasswordSequential(email);
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void result) {
//            switch(event.getType()) {
//                case OK:
//                    Toast.makeText(context, R.string.forgot_password_reset_mail_success, Toast.LENGTH_LONG).show();
//                    Fragment startupLoginFragment = getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
//                    if (startupLoginFragment == null)startupLoginFragment = StartupLoginFragment.newInstance();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.content_frame, startupLoginFragment, Constants.LOGIN_FRAGMENT)
//                            .commit();
//                    break;
//                case NOT_FOUND:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> Email was not found");
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_email_not_found), Toast.LENGTH_LONG).show();
//                    break;
//                case SERVER_ERROR:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> SERVER ERROR");
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_server_error), Toast.LENGTH_LONG).show();
//                    break;
//                case NETWORK_ERROR:
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> NETWORK ERROR");
//                    break;
//                default:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> UNDEFINED ERROR");
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.forgot_password_reset_mail_fail), Toast.LENGTH_LONG).show();
//            }
//
//            LoadingDialog.getDialog().dismiss();
//        }
//    }
//}
