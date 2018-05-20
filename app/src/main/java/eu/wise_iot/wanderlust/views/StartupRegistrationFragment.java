package eu.wise_iot.wanderlust.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.RegistrationController;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Registration Fragment which handles front end inputs of the user
 * @author Joshua
 * @license MIT
 */
public class StartupRegistrationFragment extends Fragment {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,5}$", Pattern.CASE_INSENSITIVE);
    private final String VALID_PASSWORTD_REGX = "^((?=.*\\d)(?=.*[A-Za-z]).{8,})$";

    private static final String TAG = "StartupRegisterFragment";


    private Context context;
    private Button btnRegister;
    private EditText nickNameTextfield;
    private EditText eMailTextfield;
    private EditText passwordTextfield;
    private EditText repeatedPasswordTextfield;
    private TextView redirectToLogin;

    private TextInputLayout nickNameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout passwordRepeatLayout;


    private final RegistrationController registrationController;


    /**
     * Create a standard registration fragment
     */
    public StartupRegistrationFragment() {
        this.registrationController = new RegistrationController();
    }

    public static StartupRegistrationFragment newInstance() {
        Bundle args = new Bundle();
        StartupRegistrationFragment fragment = new StartupRegistrationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startup_registration, container, false);
        btnRegister = (Button) view.findViewById(R.id.btn_signup);
        nickNameTextfield = (EditText) view.findViewById(R.id.input_nickname);
        eMailTextfield = (EditText) view.findViewById(R.id.input_mail);
        passwordTextfield = (EditText) view.findViewById(R.id.input_password);
        repeatedPasswordTextfield = (EditText) view.findViewById(R.id.input_password_repeat);
        nickNameLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_nickname);
        emailLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_mail);
        passwordLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_password);
        passwordRepeatLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_password_repeat);
        redirectToLogin = (TextView) view.findViewById(R.id.link_login);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();

        //handle keyboard closing
        view.findViewById(R.id.rootL).setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            return true;
        });
    }

    /**
     * initializes the actions of all controlls of the fragment
     */
    private void initActionControls() {
        btnRegister.setOnClickListener(v -> {
            User user = new User(0
                    , nickNameTextfield.getText().toString()
                    , eMailTextfield.getText().toString()
                    , passwordTextfield.getText().toString()
                    , 0, true, true, "", "");
            if (validateInput(user)) {
                btnRegister.setEnabled(false);
                new AsyncRegistration(user,getActivity()).execute();
            }
            // hide soft keyboard after button was clicked
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(btnRegister.getApplicationWindowToken(), 0);
        });

        redirectToLogin.setOnClickListener(v -> {

            Fragment startupLoginFragment = getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
            if (startupLoginFragment == null)startupLoginFragment = StartupLoginFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, startupLoginFragment, Constants.LOGIN_FRAGMENT)
                    .commit();
        });
    }


    /**
     * checks whether the user's data is valid and can be send to the server
     *
     * @return true if the user's data is Valid, else if invalid
     */
    private boolean validateInput(User user) {
        boolean isValid = true;
        if (user.getNickname().equals("")) {
            nickNameLayout.setError(getString(R.string.registration_username_required));
            isValid = false;
        } else {
            nickNameLayout.setError(null);
        }

        if (user.getEmail().equals("")) {
            emailLayout.setError(getString(R.string.registration_email_required));
            isValid = false;
        } else if (!validateMail(user.getEmail())) {
            emailLayout.setError(getString(R.string.registration_email_invalid));
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (user.getPassword().equals("")) {
            passwordLayout.setError(getString(R.string.registration_password_required));
            isValid = false;
        } else if (!validatePassword(user.getPassword())) {
            passwordLayout.setError(getString(R.string.registration_password_invalid));
            isValid = false;
        } else {
            passwordLayout.setError(null);
        }

        if (!user.getPassword().equals(repeatedPasswordTextfield.getText().toString())) {
            passwordRepeatLayout.setError(getString(R.string.registration_password_no_match));
            isValid = false;
        } else {
            passwordRepeatLayout.setError(null);
        }
        return isValid;
    }

    /**
     * checks whether the email address is valid or not
     *
     * @param emailStr
     * @return true if the email address is valid, else if invalid
     */
    private boolean validateMail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    /**
     * checks whether the password is valid or not
     *
     * @param password
     * @return true if the password is Valid, else if invalid
     */
    private boolean validatePassword(String password) {
        return password.matches(VALID_PASSWORTD_REGX);
    }
    /*
     * TODO do async tasks static to prevent leaks see https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
     * might not be the best solution either!
     */
    /**
     * handles async backend request for performing an asynchronous registration
     * this will keep the UI responsive
     *
     * @author Alexander Weinbeck
     * @license MIT
     */
    private class AsyncRegistration extends AsyncTask<Void, Void, Void> {
        final ProgressDialog pdLoading;
        private final Activity activity;
        private User user;
        private ControllerEvent event;

        AsyncRegistration(User user, Activity activity){
            this.user = user;
            this.activity = activity;
            pdLoading = new ProgressDialog(this.activity);
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage("\t" + getResources().getString(R.string.msg_registering));
            pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            event =  registrationController.registerUserSequential(user);
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            switch(event.getType()) {
                case OK:
                    ((MainActivity) getActivity()).setupDrawerHeader(user);
                    Toast.makeText(context, R.string.registration_email_confirmation, Toast.LENGTH_LONG).show();
                    Fragment startupLoginFragment = getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
                    if (startupLoginFragment == null)startupLoginFragment = StartupLoginFragment.newInstance();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, startupLoginFragment, Constants.LOGIN_FRAGMENT)
                            .commit();
                    break;
                case CONFLICT:
                    Toast.makeText(context, R.string.registration_nickname_mail_used, Toast.LENGTH_LONG).show();
                    break;
                case SERVER_ERROR:
                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> SERVER ERROR" + event.getType().toString());
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_server_error_get_user), Toast.LENGTH_LONG).show();
                    break;
                case NETWORK_ERROR:
                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_no_internet), Toast.LENGTH_LONG).show();
                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> NETWORK ERROR" + event.getType().toString());
                    break;
                default:
                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> UNDEFINED ERROR" + event.getType().toString());
                    Toast.makeText(context, R.string.registration_connection_error, Toast.LENGTH_LONG).show();
            }
            //make registration button available again
            btnRegister.setEnabled(true);
            if (pdLoading.isShowing()) pdLoading.dismiss();
        }
    }
}
