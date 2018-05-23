package eu.wise_iot.wanderlust.views;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EquipmentController;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.controllers.OfflineQueueController;
import eu.wise_iot.wanderlust.controllers.WeatherController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.views.animations.CircleTransform;
import io.objectbox.BoxStore;

import static android.os.Process.setThreadPriority;
import static eu.wise_iot.wanderlust.controllers.EventType.OK;

/**
 * MainActivity:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    public static BoxStore boxStore;
    public static Activity activity;
    private static BroadcastReceiver broadcastReceiver;

    private TextView username;
    private TextView email;
    private ImageView userProfileImage;
    private View header;
    private NavigationView navigationView;
    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        getFragmentManager().beginTransaction().replace(R.id.content_frame, BackgroundFragment.newInstance(), Constants.BACKGROUND_FRAGMENT).commit();

        activity = this;
        setContentView(R.layout.activity_main);
        setupNavigation();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        JodaTimeAndroid.init(this);
        DatabaseController.createInstance(getApplicationContext());
        ImageController.createInstance(getApplicationContext());
        WeatherController.createInstance(getApplicationContext());
        EquipmentController.createInstance(getApplicationContext());
        loginController = new LoginController();
        broadcastReceiver = new OfflineQueueController.NetworkChangeReceiver();

        username = (TextView) findViewById(R.id.user_name);
        email = (TextView) findViewById(R.id.user_mail_address);
        userProfileImage = (ImageView) findViewById(R.id.user_profile_image);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        this.registerReceiver(
                broadcastReceiver,
                new IntentFilter(
                        ConnectivityManager.CONNECTIVITY_ACTION));

        if (preferences.getBoolean("firstTimeOpened", true)) {
            // start welcome screen
            ft.replace(R.id.content_frame, new StartupRegistrationFragment().newInstance())
                    .commit();

            // else try to login
        } else {

            User user = UserDao.getInstance().getUser();
            if (user == null) {
                StartupLoginFragment loginFragment = new StartupLoginFragment();
                ft.replace(R.id.content_frame, loginFragment)
                        .commit();
                return;
            }

            switch (user.getAccountType()) {
                case "instagram":
                    Fragment webLoginFragment = getFragmentManager().findFragmentByTag(Constants.WEB_LOGIN_FRAGMENT);
                    if (webLoginFragment == null) webLoginFragment = WebLoginFragment.newInstance(
                            WebLoginFragment.LoginProvider.INSTAGRAM);
                    ft.replace(R.id.content_frame, webLoginFragment, Constants.WEB_LOGIN_FRAGMENT)
                            .commit();
                    break;
                case "facebook":
                    Fragment webLoginFragment2 = getFragmentManager().findFragmentByTag(Constants.WEB_LOGIN_FRAGMENT);
                    if (webLoginFragment2 == null) webLoginFragment2 = WebLoginFragment.newInstance(
                            WebLoginFragment.LoginProvider.FACEBOOK);
                    ft.replace(R.id.content_frame, webLoginFragment2, Constants.WEB_LOGIN_FRAGMENT)
                            .commit();
                    break;
                default:
                    new AsyncLoginOnLoad(new LoginUser(user.getNickname(), user.getPassword()), user,this, ft).execute();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) checkPermissions();
    }


    /**
     * Initializes the navigation and elements
     */
    private void setupNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        getSupportActionBar().show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
    /**
     * Manages drawer menu back navigation
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment fragment = getFragmentManager().findFragmentById(R.id.content_frame);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        //Don't do anything with back button if user is on login or registration screen
        else if (fragment != null && fragment instanceof StartupRegistrationFragment
                || fragment instanceof StartupLoginFragment
                || fragment instanceof MapFragment
                || fragment instanceof TourOverviewFragment
                || fragment instanceof ProfileFragment
                || fragment instanceof DisclaimerFragment
                || fragment instanceof UserGuideFragment
                || fragment instanceof StartupResetPasswordFragment) {
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Handles click events in the drawer menu. Contains all selectable links to fragments
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        String fragmentTag = null;
        int id = item.getItemId();
        FragmentManager fm = getFragmentManager();

        if (BuildConfig.DEBUG) Log.d(TAG,"FRAGMENTSTACK COUNT: " + fm.getBackStackEntryCount());

        // MAIN FRAGMENTS
        switch (id) {
            case R.id.nav_map:
                fragmentTag = Constants.MAP_FRAGMENT;
                fragment = fm.findFragmentByTag(fragmentTag);
                if (fragment == null) fragment = MapFragment.newInstance(); //create if not available yet
                break;
            case R.id.nav_tours:
                fragmentTag = Constants.TOUROVERVIEW_FRAGMENT;
                fragment = fm.findFragmentByTag(fragmentTag);
                if (fragment == null) fragment = TourOverviewFragment.newInstance();
                break;
            case R.id.nav_profile:
                fragmentTag = Constants.PROFILE_FRAGMENT;
                fragment = fm.findFragmentByTag(fragmentTag);
                if (fragment == null) fragment = ProfileFragment.newInstance();
                break;
            // OTHER FRAGMENTS
            case R.id.setup_guide:
                fragmentTag = Constants.USER_GUIDE_FRAGMENT;
                fragment = fm.findFragmentByTag(fragmentTag);
                if (fragment == null) fragment = UserGuideFragment.newInstance();
                break;
            case R.id.disclaimer:
                fragmentTag = Constants.DISCLAIMER_FRAGMENT;
                fragment = fm.findFragmentByTag(fragmentTag);
                if (fragment == null) fragment = DisclaimerFragment.newInstance();
                break;
            case R.id.logout:
                loginController.logout(controllerEvent -> {
                    switch (controllerEvent.getType()) {
                        case OK:
                            LoginUser.clearCookies();
                            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                            Fragment f = fm.findFragmentByTag(Constants.LOGIN_FRAGMENT);
                            if (f == null) f = StartupLoginFragment.newInstance();
                            switchFragment(f, Constants.LOGIN_FRAGMENT);
                            break;
                        case NETWORK_ERROR:
                            Toast.makeText(getApplicationContext(), R.string.logout_failed, Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), R.string.msg_general_error, Toast.LENGTH_LONG).show();
                            break;
                    }
                });
                return true;
        }

        switchFragment(fragment, fragmentTag);
        return true;
    }

    private void switchFragment(Fragment fragment, String fragmentTag) {
        if (fragment != null) {
            if(fragment.isAdded()){
                getFragmentManager().beginTransaction().show(fragment);
            } else {
                if(getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT).isAdded()) {
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT));
                }
                //pop the backstack without showing the fragments to not infinite add to the stack
                //null is anchor for the stack
                //getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    android.support.v4.app.FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(
                            0);
                    getSupportFragmentManager().popBackStack(entry.getId(),
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager().executePendingTransactions();
                }
                //set anchor null, not the tag of the given fragment
                getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, fragmentTag)
                        .addToBackStack(null)
                        .commit();
            }
        } else {
            Log.e(TAG, "Error in creating fragment");
            Toast.makeText(getApplicationContext(), R.string.msg_no_action_defined, Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    /**
     * Checks if device has granted permissions to access location manager and permissions to
     * write on storage. Requires API Level >= 23
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissions() {
        List<String> permissionsList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (!permissionsList.isEmpty()) {
            String[] params = permissionsList.toArray(new String[permissionsList.size()]);
            requestPermissions(params, Constants.REQUEST_FOR_MULTIPLE_PERMISSIONS);
        }
    }

    public void setupDrawerHeader(User user) {
        username = (TextView) findViewById(R.id.user_name);
        email = (TextView) findViewById(R.id.user_mail_address);
        userProfileImage = (ImageView) findViewById(R.id.user_profile_image);

        userProfileImage.setOnClickListener(view -> switchFragment(ProfileFragment.newInstance(), Constants.PROFILE_FRAGMENT));

        username.setText(user.getNickname());
        email.setText(user.getEmail());

        updateProfileImage(loginController.getProfileImage());
    }

    public void updateProfileImage(File image) {
        if (userProfileImage == null) {
            userProfileImage = (ImageView) findViewById(R.id.user_profile_image);
        }
        if (image != null) {
            Picasso.with(activity).load(image).transform(new CircleTransform()).fit().placeholder(R.drawable.progress_animation).into(userProfileImage);
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);
            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            drawable.setCircular(true);
            userProfileImage.setImageDrawable(drawable);
        }
    }


    public void updateEmailAdress(String newEmail) {
        email.setText(newEmail);
    }

    public void updateNickname(String newNickname) {
        username.setText(newNickname);
    }

    /**
     * handles async backend request for performing an asynchronous login
     * while auto logging in
     * this will keep the UI responsive
     *
     * @author Alexander Weinbeck
     * @license MIT
     */
    private class AsyncLoginOnLoad extends AsyncTask<Void, Void, ControllerEvent> {
        final ProgressDialog pdLoading;
        private final LoginUser loginUser;
        private final User user;
        private final Activity activity;
        private final FragmentTransaction ft;

        AsyncLoginOnLoad(LoginUser loginUser,User user, Activity activity, FragmentTransaction ft){
            this.loginUser = loginUser;
            this.user = user;
            this.activity = activity;
            this.ft = ft;
            pdLoading = new ProgressDialog(this.activity);
        }
        @Override
        protected void onPreExecute() {
            //this method will be running on UI thread
            pdLoading.setMessage("\t" + getResources().getString(R.string.msg_logging_in));pdLoading.setCancelable(false);
            pdLoading.show();
        }
        @Override
        protected ControllerEvent doInBackground(Void... params) {
            setThreadPriority(-10);
            return loginController.logInSequential(loginUser);
        }
        @Override
        protected void onPostExecute(ControllerEvent event) {
            User loggedInUser = (User) event.getModel();
            switch (event.getType()) {
                case OK:
                    setupDrawerHeader(loggedInUser);

                    //set last login
                    DateTimeZone timeZone = DateTimeZone.forTimeZone(TimeZone.getDefault());
                    DateTime now = new DateTime().withZone(timeZone);
                    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                    String lastLoginNow = fmt.print(now);
                    loggedInUser.setLastLogin(lastLoginNow);
                    UserDao.getInstance().update(loggedInUser);

                    getFragmentManager().beginTransaction().replace(R.id.content_frame, MapFragment.newInstance(), Constants.MAP_FRAGMENT)
                            .commit();
                    break;
                default:
                    DateTime lastLogin2 = DateTime.parse(user.getLastLogin());
                    if (BuildConfig.DEBUG) Log.d(TAG, "Last login: " + lastLogin2);
                    //check if last login is within last 24h
                    if (lastLogin2.isAfter(new DateTime().minusDays(1))) {
                        setupDrawerHeader(user);
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, MapFragment.newInstance(), Constants.MAP_FRAGMENT)
                                .commit();

                    } else {
                        //StartupLoginFragment loginFragment = new StartupLoginFragment();
                        getFragmentManager().beginTransaction().replace(R.id.content_frame, new StartupLoginFragment().newInstance())
                                .commit();
                    }
                    break;
            }

            if (pdLoading.isShowing()) pdLoading.dismiss();
        }
    }

}
