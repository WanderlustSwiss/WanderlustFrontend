package eu.wise_iot.wanderlust.views;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * MainActivity:
 * @author Fabian Schwander
 * @license MIT
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupNavigation();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        if(!DatabaseController.initialized) DatabaseController.initDaoModels(getApplicationContext());
        //TODO remove after login works
        fakeLogin(new FragmentHandler(){
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                // check if app is opened for the first time
                if (preferences.getBoolean("firstTimeOpened", true) && false) { //TODO for testing
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("firstTimeOpened", false); // save that app has been opened
                    editor.apply();

                    // start welcome screen
                    //RegistrationFragment welcomeFragment = new RegistrationFragment();
                    RegistrationFragment registrationFragment = new RegistrationFragment();
                    getFragmentManager().beginTransaction()
                            .add(R.id.content_frame, registrationFragment)
                            .commit();
                    // else start the map screen
                } else {
                    MapFragment mapFragment = MapFragment.newInstance();
                    getFragmentManager().beginTransaction()
                            .add(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                            .commit();
                }
            }
        });
    }

    private void fakeLogin(FragmentHandler handler){
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        LoginUser testUser = new LoginUser("testuser", "HalloW3lt");
        Call<LoginUser> call = loginService.basicLogin(testUser);
        call.enqueue(new Callback<LoginUser>() {
            @Override
            public void onResponse(Call<LoginUser> call, Response<LoginUser> response) {
                if (response.isSuccessful()) {

                    Headers headerResponse = response.headers();
                    //convert header to Map
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));
                    handler.onResponse(new ControllerEvent(EventType.OK));
                } else {
                }
            }

            @Override
            public void onFailure(Call<LoginUser> call, Throwable t) {
            }
        });
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
            fragment = SearchFragment.newInstance();
            fragmentTag = Constants.SEARCH_FRAGMENT;
        } else if (id == R.id.nav_profile) {
            // TODO: add ProfileFragment here
        }

        // OTHER FRAGMENTS
        else if (id == R.id.nav_manual) {
            fragment = new ManualFragment();
            fragmentTag = Constants.MANUAL_FRAGMENT;
        } else if (id == R.id.nav_about) {
            fragment = new DisclaimerFragment();
            fragmentTag = Constants.DISCLAIMER_FRAGMENT;
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
