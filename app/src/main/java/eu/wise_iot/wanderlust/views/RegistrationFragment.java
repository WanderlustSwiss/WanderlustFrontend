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


public class RegistrationFragment extends Fragment {

    private Context context;
    private Button btnRegister;
    private EditText nickNameTextfield;
    private EditText eMailTextfield;
    private EditText passwordTextfield;
    private EditText repeatedPasswordTextfield;
    private TextView redirectToLogin;

    private RegistrationController registrationController;

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
                if (validateInput()) {
                    registrationController.registerUser(null, new FragmentHandler() {
                        @Override
                        public void onResponse(Event event) {
                            Event.EventType eventType = event.getType();
                            switch (eventType){
                                case SUCCESSFUL:

                                    break;

                                default:

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
        if (nickNameTextfield.getText().equals("")
                || eMailTextfield.getText() .equals("")
                || passwordTextfield.getText().equals("")
                || repeatedPasswordTextfield.getText().equals("")) {
            Toast.makeText(context, "Bitte alle Felder ausfüllen", Toast.LENGTH_LONG).show();
            isValid = false;
        } else if(!passwordTextfield.equals(repeatedPasswordTextfield)){
         //   Toast.makeText(context, "Die Passwörter stimmen nicht überein", Toast.LENGTH_LONG).show();
        }
        return isValid;
    }
}
