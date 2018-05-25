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
import android.util.Log;
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

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.views.controls.LoadingDialog;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Login Fragment which handles front end inputs of the user for login
 *
 * @author Joshua Meier
 * @license MIT
 */
public class StartupLoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "StartupLoginFragment";

    private static final int REQ_CODE = 9001;
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


    /**
     * Create a standard login fragment
     */
    public StartupLoginFragment() {
        loginController = new LoginController();
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
        btnLogin = view.findViewById(R.id.btn_signin);
        btnInstagram = view.findViewById(R.id.web_login_instagram_button);
        btnFacebook = view.findViewById(R.id.web_login_facebook_button);
        nicknameEmailTextfield = view.findViewById(R.id.input_nickname_email);
        nicknameEmailLayout = view.findViewById(R.id.text_input_layout_nickname_email);
//        signInButtonGoogle = (SignInButton) view.findViewById(R.id.sign_in_button);
        redirectToRegistration = view.findViewById(R.id.link_registration);
        forgotPassword = view.findViewById(R.id.login_forgetPassword);
        passwordTextfield = view.findViewById(R.id.input_password);
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

            LoadingDialog.getDialog().show(getActivity());
            loginController.logIn(loginUser, event -> {
                switch (event.getType()) {
                    case OK:
                        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                        User user = (User) event.getModel();
                        ((MainActivity) getActivity()).setupDrawerHeader(user);
                        if (preferences.getBoolean("firstTimeOpened", true)) {
                            preferences.edit().putBoolean("firstTimeOpened", false).apply(); // save that app has been opened

                            Fragment userGuideFragment = getFragmentManager().findFragmentByTag(Constants.USER_GUIDE_FRAGMENT);
                            if (userGuideFragment == null)
                                userGuideFragment = UserGuideFragment.newInstance();

                            getFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, userGuideFragment, Constants.USER_GUIDE_FRAGMENT)
                                    //.addToBackStack(Constants.USER_GUIDE_FRAGMENT)
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
                    case NOT_FOUND:
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "ERROR: Server Response arrived -> User was not found");
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.msg_user_not_found), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getText(R.string.msg_general_error), Toast.LENGTH_LONG).show();
                        break;

                }

                LoadingDialog.getDialog().dismiss();

                btnLogin.setEnabled(true);

                // hide soft keyboard after button was clicked
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(btnLogin.getApplicationWindowToken(), 0);
            });

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
            if (startupRegistrationFragment == null)
                startupRegistrationFragment = StartupRegistrationFragment.newInstance();
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
            if (startupResetPasswordFragment == null)
                startupResetPasswordFragment = StartupResetPasswordFragment.newInstance();
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
            Toast.makeText(context, getString(R.string.msg_hello) + ' ' + account.getGivenName() + ' ' + account.getFamilyName(), Toast.LENGTH_LONG).show();

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
    /**
     * handles async backend request for performing an asynchronous login when clicking on login
     * this will keep the UI responsive
     *
     * @author Alexander Weinbeck
     * @license MIT
     */
//    private class AsyncLoginOnClick extends AsyncTask<Void, Void, Void> {
//        private final LoginUser user;
//        private ControllerEvent event;
//
//        AsyncLoginOnClick(LoginUser user){
//            this.user = user;
//        }
//        @Override
//        protected void onPreExecute() {
//            LoadingDialog.getDialog().show(getActivity());
//        }
//        @Override
//        protected Void doInBackground(Void... params) {
//            setThreadPriority(-10);
//            event =  loginController.logInSequential(user);
//            return null;
//        }
//        @Override
//        protected void onPostExecute(Void result) {
//            switch(event.getType()) {
//                case OK:
//                    SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
//                    User user = (User) event.getModel();
//                    ((MainActivity)getActivity()).setupDrawerHeader(user);
//                    if(preferences.getBoolean("firstTimeOpened", true)) {
//                        preferences.edit().putBoolean("firstTimeOpened", false).apply(); // save that app has been opened
//
//                        Fragment userGuideFragment = getFragmentManager().findFragmentByTag(Constants.USER_GUIDE_FRAGMENT);
//                        if (userGuideFragment == null) userGuideFragment = UserGuideFragment.newInstance();
//
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.content_frame, userGuideFragment, Constants.USER_GUIDE_FRAGMENT)
//                                .addToBackStack(Constants.USER_GUIDE_FRAGMENT)
//                                .commit();
//                    } else {
//
//                        Fragment mapFragment = getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
//                        if (mapFragment == null) mapFragment = MapFragment.newInstance();
//                        getFragmentManager().beginTransaction()
//                                .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
//                                .commit();
//                        //((AppCompatActivity) getActivity()).getSupportActionBar().show();
//                    }
//
//                    break;
//                case NOT_FOUND:
//                    if (BuildConfig.DEBUG) Log.d(TAG,"ERROR: Server Response arrived -> User was not found");
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_user_not_found), Toast.LENGTH_LONG).show();
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
//                    Toast.makeText(getActivity().getApplicationContext(),getResources().getText(R.string.msg_general_error), Toast.LENGTH_LONG).show();
//            }
//
//            LoadingDialog.getDialog().dismiss();
//        }
//    }
//}


