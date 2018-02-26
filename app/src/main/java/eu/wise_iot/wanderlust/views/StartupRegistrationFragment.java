package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
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

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.RegistrationController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;

/*
 * Registration Fragment which handles front end inputs of the user
 * @author Joshua
 * @license MIT
 */
public class StartupRegistrationFragment extends Fragment {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,5}$", Pattern.CASE_INSENSITIVE);
    private final String VALID_PASSWORTD_REGX = "^((?=.*\\d)(?=.*[A-Za-z]).{8,})$";


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


    private RegistrationController registrationController;


    /**
     * Create a standard registration fragment
     */
    public StartupRegistrationFragment() {
        this.registrationController = new RegistrationController();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        }
        context = getActivity();
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

    }

    /**
     * initializes the actions of all controlls of the fragment
     */
    private void initActionControls() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(0
                        , nickNameTextfield.getText().toString()
                        , eMailTextfield.getText().toString()
                        , passwordTextfield.getText().toString()
                        , 0, true, true, "", "");
                if (validateInput(user)) {
                    //get response
                    registrationController.registerUser(user, new FragmentHandler() {
                        @Override
                        public void onResponse(ControllerEvent controllerEvent) {
                            EventType eventType = controllerEvent.getType();
                            switch (eventType) {
                                case OK:
                                    Toast.makeText(context, R.string.registration_email_confirmation, Toast.LENGTH_LONG).show();
                                    StartupLoginFragment startupLoginFragment = new StartupLoginFragment();
                                    getFragmentManager().beginTransaction()
                                            .add(R.id.content_frame, startupLoginFragment)
                                            .commit();

                                    // create profile for user if registration succesful
                                    Profile profile = new Profile(0, user.getProfile(),
                                            (byte) 1, 0, 2, "",
                                                        "de", user.getUser_id(), 0);
                                    break;
                                case CONFLICT:
                                    Toast.makeText(context, R.string.registration_nickname_mail_used, Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(context, R.string.registration_connection_error, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                // hide soft keyboard after button was clicked
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(btnRegister.getApplicationWindowToken(), 0);
            }
        });

        redirectToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartupLoginFragment startupLoginFragment = new StartupLoginFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, startupLoginFragment)
                        .commit();
            }
        });
    }


    /**
     * checks whether the user's data is valid and can be send to the servert
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

}
