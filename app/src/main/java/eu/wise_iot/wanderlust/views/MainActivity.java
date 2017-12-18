package eu.wise_iot.wanderlust.views;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.views.dialog.EditPoiDialog;
import eu.wise_iot.wanderlust.views.dialog.ViewPoiDialog;
import io.objectbox.BoxStore;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    public static BoxStore boxStore;
    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigation();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        DatabaseController.initDaoModels(getApplicationContext());
        DatabaseController.clearAllDownloadedImages();
        DatabaseController.poiDao.poiBox.removeAll();
        loginController = new LoginController();

        if (preferences.getBoolean("firstTimeOpened", true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstTimeOpened", false); // save that app has been opened
            editor.apply();

            // start welcome screen
            RegistrationFragment registrationFragment = new RegistrationFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.content_frame, registrationFragment)
                    .commit();


            // else try to login
        } else {
            User user = DatabaseController.userDao.getUser();
            if (user == null) {
                LoginFragment loginFragment = new LoginFragment();
                getFragmentManager().beginTransaction()
                        .add(R.id.content_frame, loginFragment)
                        .commit();
                return;
            }
            loginController.logIn(new LoginUser(user.getNickname(), user.getPassword()), new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()) {
                        case OK:
                            MapFragment mapFragment = MapFragment.newInstance();
                            getFragmentManager().beginTransaction()
                                    .add(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                                    .commit();
                            break;
                        default:
                            LoginFragment loginFragment = new LoginFragment();
                            getFragmentManager().beginTransaction()
                                    .add(R.id.content_frame, loginFragment)
                                    .commit();
                    }
                }
            });
        }
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Manages drawer menu back navigation
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

        // MAIN FRAGMENTS
        if (id == R.id.nav_map) {
            fragment = MapFragment.newInstance();
            fragmentTag = Constants.MAP_FRAGMENT;
        } else if (id == R.id.nav_tours) {
            // TODO: add TourOverviewFragment here
            Toast.makeText(getApplicationContext(), R.string.msg_no_action_defined, Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_profile) {
            fragment = ProfileFragment.newInstance();
            fragmentTag = Constants.PROFILE_FRAGMENT;
        }

        // OTHER FRAGMENTS
        else if (id == R.id.logout) {
            loginController.logout(new FragmentHandler() {
                @Override
                public void onResponse(ControllerEvent controllerEvent) {
                    switch (controllerEvent.getType()) {
                        case OK:
                            LoginUser.clearCookies();
                            Toast.makeText(getApplicationContext(), "logout successful", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "logout failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
            fragment = new LoginFragment();
            fragmentTag = Constants.LOGIN_FRAGMENT;
        }

        if (fragment != null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment, fragmentTag)
                    .addToBackStack(null)
                    .commit();
        } else {
            Log.e(TAG, "Error in creating fragment");
            Toast.makeText(getApplicationContext(), R.string.msg_no_action_defined, Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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


}
