package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;

/*
 * Login Fragment which handles front end inputs of the user for login
 * @author Joshua
 * @license MIT
 */
public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{

    private Context context;

    private Button btnLogin;
    private EditText nicknameEmailTextfield;
    private EditText passwordTextfield;
    private TextInputLayout nicknameEmailLayout;
    private SignInButton signInButtonGoogle;
    private TextView redirectToRegistration;


    private GoogleApiClient googleApiClient;
    private LoginUser loginUser;
    public static int REQ_CODE = 9001;


    private LoginController loginController;

    private FragmentHandler fragmentHandler =  new FragmentHandler() {
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
        };
    };



    /**
     * Create a standard login fragment
     */
    public LoginFragment() {
        this.loginController = new LoginController(this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage((FragmentActivity) getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = (Button) view.findViewById(R.id.btn_signin);
        nicknameEmailTextfield = (EditText) view.findViewById(R.id.input_nickname_email);
        nicknameEmailLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_nickname_email);
        nicknameEmailLayout.setErrorEnabled(true);
        signInButtonGoogle = (SignInButton) view.findViewById(R.id.sign_in_button);
        redirectToRegistration = (TextView) view.findViewById(R.id.link_registration);


        passwordTextfield = (EditText) view.findViewById(R.id.input_password);
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
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser = new LoginUser(
                        nicknameEmailTextfield.getText().toString(),
                        passwordTextfield.getText().toString()
                );

                loginController.logIn(loginUser, fragmentHandler);
            }
        });

        signInButtonGoogle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        redirectToRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleApiClient.stopAutoManage((FragmentActivity) getActivity());
                googleApiClient.disconnect();
                RegistrationFragment registrationFragment = new RegistrationFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, registrationFragment)
                        .commit();
            }
        });
    }
    /**
     * Starts the sign in process with via Google API
     */
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleGoogleSignInResult(result);


        }
    }
    /**
     * Handles the result received from the Google API for user trying to login with a Google account
     */
    private void handleGoogleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String token = account.getIdToken();
            Toast.makeText(context, "Hello " + account.getGivenName() + " " + account.getFamilyName(), Toast.LENGTH_LONG).show();

            LoginUser user = new LoginUser(account.getEmail(), token);
         //   loginController.logIn(user, fragmentHandler);
        } else {

            Toast.makeText(context, "fail", Toast.LENGTH_LONG).show();

        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


