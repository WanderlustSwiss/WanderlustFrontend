package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.RegistrationController;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;


public class RegistrationFragment extends Fragment {

    private final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,5}$", Pattern.CASE_INSENSITIVE);
    private final String VALID_PASSWORTD_REGX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";

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

    private User user;

    public RegistrationFragment() {
        this.registrationController = new RegistrationController(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration, container, false);
        btnRegister = (Button) view.findViewById(R.id.btn_signup);
        nickNameTextfield = (EditText) view.findViewById(R.id.input_nickname);
        eMailTextfield = (EditText) view.findViewById(R.id.input_mail);
        passwordTextfield = (EditText) view.findViewById(R.id.input_password);
        repeatedPasswordTextfield = (EditText) view.findViewById(R.id.input_password_repeat);
        nickNameLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_nickname);
        nickNameLayout.setErrorEnabled(true);
        emailLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_mail);
        emailLayout.setErrorEnabled(true);
        passwordLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_password);
        passwordLayout.setErrorEnabled(true);
        passwordRepeatLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_password_repeat);
        passwordRepeatLayout.setErrorEnabled(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();

    }


    private void initActionControls() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new User(0
                        , nickNameTextfield.getText().toString()
                        , eMailTextfield.getText().toString()
                        , passwordTextfield.getText().toString()
                        , 0, true, true, "", "");
                if (validateInput()) {


                    registrationController.registerUser(user, new FragmentHandler() {
                        @Override
                        public void onResponse(Event event) {
                            EventType eventType = event.getType();
                            switch (eventType) {
                                case OK:
                                    MapFragment tourFragment = new MapFragment();
                                    getFragmentManager().beginTransaction()
                                            .add(R.id.content_frame, tourFragment)
                                            .commit();
                                    break;
                                case CONFLICT:
                                    Toast.makeText(context, R.string.registration_nickname_mail_used, Toast.LENGTH_LONG).show();
                                    break;
                                case BAD_REQUEST:
                                    Toast.makeText(context, R.string.registration_connection_error, Toast.LENGTH_LONG).show();
                                    break;
                                case SERVER_ERROR:
                                    Toast.makeText(context, R.string.registration_connection_error, Toast.LENGTH_LONG).show();
                                    break;
                                case NOT_FOUND:
                                    Toast.makeText(context, R.string.registration_connection_error, Toast.LENGTH_LONG).show();
                                    break;
                                case NETWORK_ERROR:
                                    Toast.makeText(context, R.string.registration_connection_error, Toast.LENGTH_LONG).show();
                                    break;
                            }

                        }
                    });
                }
            }
        });
    }


    /*** Validates the input of the user. And
     * @return true if the input is valid
     */
    private boolean validateInput() {
        boolean isValid = true;
        if (user.getNickname().equals("")) {
            nickNameLayout.setError("bla");
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
        }else {
            emailLayout.setError(null);
        }

        if (user.getPassword().equals("")) {
            passwordLayout.setError(getString(R.string.registration_password_required));
            isValid = false;
        } else if(!validatePassword(user.getPassword())){
            passwordLayout.setError(getString(R.string.registration_password_invalid));
            isValid = false;
        }else {
            passwordLayout.setError(null);
        }

        if (!user.getPassword().equals(repeatedPasswordTextfield.getText().toString())) {
            passwordRepeatLayout.setError(getString(R.string.registration_password_no_match));
            isValid = false;
        }else {
            passwordRepeatLayout.setError(null);
        }
        return isValid;
    }

    private boolean validateMail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    private boolean validatePassword(String password){
        return password.matches(VALID_PASSWORTD_REGX);
    }

}