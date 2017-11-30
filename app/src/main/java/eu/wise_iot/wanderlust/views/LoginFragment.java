package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;


public class LoginFragment extends Fragment {

    private Context context;

    private Button btnLogin;
    private EditText nicknameEmailTextfield;
    private EditText passwordTextfield;
    private TextInputLayout nicknameEmailLayout;

    private LoginUser loginUser;

    private LoginController loginController;


    public LoginFragment() {
        this.loginController = new LoginController(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = (Button) view.findViewById(R.id.btn_signin);
        nicknameEmailTextfield = (EditText) view.findViewById(R.id.input_nickname_email);
        nicknameEmailLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_nickname_email);
        nicknameEmailLayout.setErrorEnabled(true);

        passwordTextfield = (EditText) view.findViewById(R.id.input_password);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionControls();

    }


    private void initActionControls() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser = new LoginUser(
                        nicknameEmailTextfield.getText().toString(),
                        passwordTextfield.getText().toString()
                );

                loginController.logIn(loginUser, new FragmentHandler() {
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
                                nicknameEmailLayout.setError(getString(R.string.login_failure));
                                break;
                            default:
                            Toast.makeText(context, R.string.registration_connection_error, Toast.LENGTH_LONG).show();
                            break;

                        }
                    }
                });
            }
        });
    }
}


