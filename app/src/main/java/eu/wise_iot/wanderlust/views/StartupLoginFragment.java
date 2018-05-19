package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Login Fragment which handles front end inputs of the user for login
 *
 * @author Joshua
 * @license MIT
 */
public class StartupLoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "StartupLoginFragment";

    public static final int REQ_CODE = 9001;
    private Context context;
    private Button btnLogin;
    private Button btnInstagram;
    private Button btnFacebook;
    private EditText nicknameEmailTextfield;
    private EditText passwordTextfield;
    private TextInputLayout nicknameEmailLayout;
    private SignInButton signInButtonGoogle;
    private TextView redirectToRegistration;
    private TextView forgotPassword;
    //    private GoogleApiClient googleApiClient;
    private LoginUser loginUser;
    private final LoginController loginController;
    private final FragmentHandler fragmentHandler = new FragmentHandler() {
        @Override
        public void onResponse(ControllerEvent event) {

            //Enable LoginButton after request is complete
            btnLogin.setEnabled(true);

            switch (event.getType()) {
                case OK:
                    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    User user = (User) event.getModel();
                    ((MainActivity) getActivity()).setupDrawerHeader(user);
                    if(preferences.getBoolean("firstTimeOpened", true)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("firstTimeOpened", false); // save that app has been opened
                        editor.apply();

                        Fragment userGuideFragment = getFragmentManager().findFragmentByTag(Constants.USER_GUIDE_FRAGMENT);
                        if (userGuideFragment == null)userGuideFragment = UserGuideFragment.newInstance();

                        getFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, userGuideFragment, Constants.USER_GUIDE_FRAGMENT)
                                .addToBackStack(Constants.USER_GUIDE_FRAGMENT)
                                .commit();
                    } else {

                        Fragment mapFragment = getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
                        if (mapFragment == null) mapFragment = MapFragment.newInstance();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                                .commit();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }

                    break;
                default:
                    Toast.makeText(context, event.getType().toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    /**
     * Create a standard login fragment
     */
    public StartupLoginFragment() {
        this.loginController = new LoginController();
    }

    public static StartupLoginFragment newInstance() {
        Bundle args = new Bundle();
        StartupLoginFragment fragment = new StartupLoginFragment();
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

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .build();
//
//        googleApiClient = new GoogleApiClient.Builder(getActivity())
//                .enableAutoManage((FragmentActivity) getActivity(), this)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startup_login, container, false);
        btnLogin = (Button) view.findViewById(R.id.btn_signin);
        btnInstagram = (Button) view.findViewById(R.id.web_login_instagram_button);
        btnFacebook = (Button) view.findViewById(R.id.web_login_facebook_button);
        nicknameEmailTextfield = (EditText) view.findViewById(R.id.input_nickname_email);
        nicknameEmailLayout = (TextInputLayout) view.findViewById(R.id.text_input_layout_nickname_email);
//        signInButtonGoogle = (SignInButton) view.findViewById(R.id.sign_in_button);
        redirectToRegistration = (TextView) view.findViewById(R.id.link_registration);
        forgotPassword = (TextView) view.findViewById(R.id.login_forgetPassword);
        passwordTextfield = (EditText) view.findViewById(R.id.input_password);
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

    /**
     * initializes the actions of all controlls of the fragment
     */
    private void initActionControls() {
        btnLogin.setOnClickListener(v -> {

            //Disable LoginButton until request is complete
            btnLogin.setEnabled(false);

            loginUser = new LoginUser(
                    nicknameEmailTextfield.getText().toString(),
                    passwordTextfield.getText().toString()
            );

            loginController.logIn(loginUser, fragmentHandler);

            // hide soft keyboard after button was clicked
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(btnLogin.getApplicationWindowToken(), 0);
        });


//        signInButtonGoogle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                signInWithGoogle();
//                // TODO: remove
//                Toast.makeText(context, R.string.msg_no_action_defined, Toast.LENGTH_LONG).show();
//            }
//        });


        redirectToRegistration.setOnClickListener(v -> {
            //googleApiClient.stopAutoManage((FragmentActivity) getActivity());
            //googleApiClient.disconnect();
            Fragment startupRegistrationFragment = getFragmentManager().findFragmentByTag(Constants.REGISTRATION_FRAGMENT);
            if (startupRegistrationFragment == null)startupRegistrationFragment = StartupRegistrationFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, startupRegistrationFragment, Constants.REGISTRATION_FRAGMENT)
                    .addToBackStack(Constants.REGISTRATION_FRAGMENT)
                    .commit();
        });
        btnInstagram.setOnClickListener(v -> {
            Fragment webLoginFragment = getFragmentManager().findFragmentByTag(Constants.WEB_LOGIN_FRAGMENT);
            if (webLoginFragment == null) webLoginFragment = WebLoginFragment.newInstance(
                    WebLoginFragment.LoginProvider.INSTAGRAM);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, webLoginFragment, Constants.WEB_LOGIN_FRAGMENT)
                    .addToBackStack(Constants.WEB_LOGIN_FRAGMENT)
                    .commit();
        });
        btnFacebook.setOnClickListener(v -> {
            Fragment webLoginFragment = getFragmentManager().findFragmentByTag(Constants.WEB_LOGIN_FRAGMENT);
            if (webLoginFragment == null) webLoginFragment = WebLoginFragment.newInstance(
                    WebLoginFragment.LoginProvider.FACEBOOK);
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, webLoginFragment, Constants.WEB_LOGIN_FRAGMENT)
                    .addToBackStack(Constants.WEB_LOGIN_FRAGMENT)
                    .commit();
        });
        forgotPassword.setOnClickListener(v -> {
            //googleApiClient.stopAutoManage((FragmentActivity) getActivity());
            //googleApiClient.disconnect();

            Fragment startupResetPasswordFragment = getFragmentManager().findFragmentByTag(Constants.RESET_PASSWORD_FRAGMENT);
            if (startupResetPasswordFragment == null)startupResetPasswordFragment = StartupResetPasswordFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, startupResetPasswordFragment, Constants.RESET_PASSWORD_FRAGMENT)
                    .addToBackStack(Constants.RESET_PASSWORD_FRAGMENT)
                    .commit();
        });
    }

//    /**
//     * Starts the sign in process with via Google API
//     */
//    private void signInWithGoogle() {
//        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//        startActivityForResult(signInIntent, REQ_CODE);
//    }

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
    private void handleGoogleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String token = account.getIdToken();
            Toast.makeText(context, getString(R.string.msg_hello) + " " + account.getGivenName() + " " + account.getFamilyName(), Toast.LENGTH_LONG).show();

            LoginUser user = new LoginUser(account.getEmail(), token);
            //   loginController.logIn(user, fragmentHandler);
        } else {
            Toast.makeText(context, R.string.login_failure, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


