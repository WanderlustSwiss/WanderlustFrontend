package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.RegistrationController;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;


public class RegistrationFragment extends Fragment {

    private Context context;
    private Button btnRegister;
    private EditText nickNameTextfield;
    private EditText eMailTextfield;
    private EditText passwordTextfield;
    private EditText repeatedPasswordTextfield;
    private TextView redirectToLogin;

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
                    
                }
            }
        });
    }


    /*** Validates the input of the user. And
     * @return true if the input is valid
     */
    private boolean validateInput() {
        boolean isValid = true;
        String text = "";
        if (user.getNickname().equals("")) {
            isValid = false;
        }

        if (user.getEmail().equals("")) {
            isValid = false;

        } else if (false) {

        }
        if (user.getPassword().equals("")) {
            isValid = false;

        } else if (user.getPassword().equals(repeatedPasswordTextfield.getText().toString())) {
            isValid = false;

        }


        return isValid;
    }

}