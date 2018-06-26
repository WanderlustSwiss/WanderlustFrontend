package eu.wise_iot.wanderlust.views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.TimeZone;

import eu.wise_iot.wanderlust.BuildConfig;
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
 * Web-login Fragment which handles front end inputs of the user for login
 *
 * @author Simon Kaspar
 * @license GPL-3.0
 */
public class WebLoginFragment extends Fragment  {
    public enum LoginProvider {
        INSTAGRAM(
                "/auth/login/instagram",
                "instagram?code=",
                R.drawable.instagram,
                R.string.login_instagram),
        FACEBOOK(
                "/auth/login/facebook",
                "facebook?code=",
                R.drawable.facebook,
                R.string.login_facebook);

        private final String path;
        private final String pattern;
        private final int loadDrawableId;
        private final int loadStringId;

        LoginProvider(String path, String pattern, int loadDrawableId, int loadStringId){
            this.path = path;
            this.pattern = pattern;
            this.loadDrawableId = loadDrawableId;
            this.loadStringId = loadStringId;
        }
        String getPath(){
            return path;
        }
        String getPattern(){
            return pattern;
        }
        int getLoadDrawableId(){
            return loadDrawableId;
        }
        int getLoadStringId(){
            return loadStringId;
        }
    }
    private static final String TAG = "WebLoginFragment";
    private static LoginProvider provider;
    private Activity context;
    private WebView webview;
    private ImageView loadDrawable;
    private TextView loadString;
    private LinearLayout instagramContainer;
    private final String target_url;
    private final LoginController loginController;
    private final FragmentHandler fragmentHandler = new FragmentHandler() {
        @Override
        public void onResponse(ControllerEvent event) {

            EventType eventType = event.getType();
            switch (eventType) {
                case OK:
                    if (BuildConfig.DEBUG) Log.d(TAG, getActivity() != null ? "not null" : "null");
                    SharedPreferences preferences = context.getPreferences(Context.MODE_PRIVATE);
                    User user = (User) event.getModel();
                    ((MainActivity) context).setupDrawerHeader(user);
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
                        DateTimeZone timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault());
                        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                        String lastLoginNow = fmt.print(new DateTime().withZone(timeZone));
                        user.setLastLogin(lastLoginNow);
                        UserDao.getInstance().update(user);

                        ((MainActivity)getActivity()).setupDrawerHeader(user);
                        Fragment mapFragment = getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
                        if (mapFragment == null) mapFragment = MapFragment.newInstance();
                        getFragmentManager().beginTransaction()
                                .replace(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                                .commit();
                        //((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }

                    break;
                default:
                    if (BuildConfig.DEBUG) Log.d("ERROR", eventType.toString());
                    Toast.makeText(context, eventType.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };


    /**
     * Create a standard login fragment
     */
    public WebLoginFragment() {
        target_url = ServiceGenerator.API_BASE_URL + provider.getPath();
        loginController = new LoginController();
    }

    public static WebLoginFragment newInstance(LoginProvider prov) {
        Bundle args = new Bundle();
        provider = prov;
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
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_web_login, container, false);
        instagramContainer = view.findViewById(R.id.web_login_provider_layout);
        loadDrawable = view.findViewById(R.id.web_login_provider_image);
        loadString = view.findViewById(R.id.web_login_provider_text);
        loadDrawable.setImageDrawable(getResources().getDrawable(provider.loadDrawableId));
        loadString.setText(getResources().getString(provider.loadStringId));
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        webview = view.findViewById(R.id.web_login);
        webview.setBackgroundColor(getResources().getColor(R.color.primary_main));
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        LoginUser emptyUser = new LoginUser(null, null);
        loginController.setDeviceInfo(emptyUser);
        String deviceParams = emptyUser.getDeviceStatisticsUrl();
        String urlWithParams = target_url + deviceParams;
        if (isNetworkAvailable()) {
            webview.setWebViewClient(new WebViewClient() {
                public void onReceivedHttpError(WebView view, WebResourceRequest request,
                                                WebResourceResponse errorRyesponse) {

                    if (BuildConfig.DEBUG) Log.d("ERROR" , "Name: " + errorRyesponse.toString() + "/ Code: " + errorRyesponse.getStatusCode());

                    Fragment loginFragment = context.getFragmentManager().findFragmentByTag(Constants.LOGIN_FRAGMENT);
                    if (loginFragment == null) loginFragment = StartupLoginFragment.newInstance();
                    context.getFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, loginFragment, Constants.LOGIN_FRAGMENT)
                            .commit();
                    ((AppCompatActivity) context).getSupportActionBar().show();
                }

                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    LoginUser.clearCookies();
                    instagramContainer.setVisibility(View.GONE);
                    String webUrl = webview.getUrl();
                    String cookies = CookieManager.getInstance().getCookie(url);
                    if (webUrl.contains(provider.getPattern())) {
                        loginController.logInWithExternalProvider(cookies, fragmentHandler);
                    }
                }
            });
            webview.loadUrl(urlWithParams);
        } else {
            User user = loginController.getAvailableUser();
            DateTime lastLogin = DateTime.parse(user.getLastLogin());
            DateTime timerLimit = new DateTime();
            timerLimit = timerLimit.minusDays(1);
            if (BuildConfig.DEBUG) Log.d(TAG, "Last login: " + lastLogin);

            if (lastLogin.isAfter(timerLimit)) {
                ((MainActivity)context).setupDrawerHeader(user);
                MapFragment fragment = MapFragment.newInstance();
                context.getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, fragment, Constants.MAP_FRAGMENT)
                        .commit();
                ((AppCompatActivity)context).getSupportActionBar().show();
            } else {
                StartupLoginFragment loginFragment = new StartupLoginFragment();
                context.getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, loginFragment)
                        .commit();
            }
        }
        return view;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

