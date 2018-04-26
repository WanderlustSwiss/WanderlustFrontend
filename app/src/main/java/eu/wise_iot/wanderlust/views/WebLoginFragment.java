package eu.wise_iot.wanderlust.views;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.TimeZone;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.services.ServiceGenerator;

/**
 * Weblogin Fragment which handles front end inputs of the user for login
 *
 * @author Simon Kaspar
 * @license MIT
 */
public class WebLoginFragment extends Fragment  {
    private static final String TAG = "WebLoginFragment";

    private Context context;
    private WebView webview;
    private LinearLayout instagramContainer;
    private final String target_url;
    private LoginUser loginUser;
    private final LoginController loginController;
    private final FragmentHandler fragmentHandler = new FragmentHandler() {
        @Override
        public void onResponse(ControllerEvent event) {

            EventType eventType = event.getType();
            switch (eventType) {
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
                                .addToBackStack(Constants.USER_GUIDE_FRAGMENT)
                                .replace(R.id.content_frame, userGuideFragment, Constants.USER_GUIDE_FRAGMENT)
                                .commit();
                    } else {

                        //set last login
                        DateTime now = new DateTime();
                        DateTimeZone timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault());
                        now = now.withZone(timeZone);
                        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                        String lastLoginNow = fmt.print(now);
                        user.setLastLogin(lastLoginNow);
                        UserDao.getInstance().update(user);

                        ((MainActivity)getActivity()).setupDrawerHeader(user);
                        Fragment mapFragment = getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
                        if (mapFragment == null) mapFragment = MapFragment.newInstance();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                                .commit();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }

                    break;
                default:
                    Toast.makeText(context, eventType.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    /**
     * Create a standard login fragment
     */
    public WebLoginFragment() {
        this.target_url = ServiceGenerator.API_BASE_URL + "/auth/login/instagram";
        this.loginController = new LoginController();
    }

    public static WebLoginFragment newInstance() {
        Bundle args = new Bundle();
        WebLoginFragment fragment = new WebLoginFragment();
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
        View view = inflater.inflate(R.layout.fragment_web_login, container, false);
        instagramContainer = (LinearLayout) view.findViewById(R.id.web_login_instagram_layout);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        webview = (WebView) view.findViewById(R.id.web_login);
        webview.setBackgroundColor(getResources().getColor(R.color.primary_main));
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        if (isNetworkAvailable()) {
            webview.setWebViewClient(new WebViewClient() {
                public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                                WebResourceResponse errorResponse) {

                    Fragment loginFragment = getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
                    if (loginFragment == null) loginFragment = StartupLoginFragment.newInstance();
                    getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, loginFragment, Constants.LOGIN_FRAGMENT)
                            .commit();
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    instagramContainer.setVisibility(View.GONE);
                    String webUrl = webview.getUrl();
                    String cookies = CookieManager.getInstance().getCookie(url);
                    if (webUrl.contains("instagram?code=")) {
                        loginController.logInInstagram(cookies, fragmentHandler);
                    }
                }
            });
            webview.loadUrl(target_url);
        } else {
            User user = loginController.getAvailableUser();
            DateTime lastLogin = DateTime.parse(user.getLastLogin());
            DateTime timerLimit = new DateTime();
            timerLimit = timerLimit.minusDays(1);
            Log.d(TAG, "Last login: " + lastLogin);

            if (lastLogin.isAfter(timerLimit)) {
                ((MainActivity)getActivity()).setupDrawerHeader(user);
                MapFragment fragment = MapFragment.newInstance();
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, fragment, Constants.MAP_FRAGMENT)
                        .commit();
                ((AppCompatActivity)getActivity()).getSupportActionBar().show();
            } else {
                StartupLoginFragment loginFragment = new StartupLoginFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, loginFragment)
                        .commit();
            }
        }
        return view;
    }

    @Override
    public void onViewCreated (View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
