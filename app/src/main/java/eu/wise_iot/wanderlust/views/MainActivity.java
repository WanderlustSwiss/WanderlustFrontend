package eu.wise_iot.wanderlust.views;

import android.Manifest;
import android.accounts.NetworkErrorException;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.services.AddCookiesInterceptor;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ReceivedCookiesInterceptor;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.UserService;
import io.objectbox.BoxStore;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MainActivity:
 * @author Fabian Schwander
 * @license MIT
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";

    public static BoxStore boxStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        setupNavigation();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);


        //TODO where to put this?
        boxStore = MyObjectBox.builder().androidContext(getApplicationContext()).build();

        // check if app is opened for the first time
        if (preferences.getBoolean("firstTimeOpened", true) || true) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstTimeOpened", false); // save that app has been opened
            editor.apply();

            ProfileFragment profileFragment = new ProfileFragment();
            getFragmentManager().beginTransaction()
                                .add(R.id.content_frame, profileFragment)
                                .commit();
/*
            // start welcome screen
            RegistrationFragment welcomeFragment = new RegistrationFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.content_frame, welcomeFragment)
                    .commit();*/
            // else start the map screen
        } else {
            MapFragment mapFragment = MapFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.content_frame, mapFragment, Constants.MAP_FRAGMENT)
                    .commit();
        }
        //login();
    }

    //TODO move to login view
    private void login(){
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        LoginUser testUser = new LoginUser("zumsel128", "Ha11loW3lt");
        Call<LoginUser> call = loginService.basicLogin(testUser);
        call.enqueue(new Callback<LoginUser>() {
            @Override
            public void onResponse(Call<LoginUser> call, Response<LoginUser> response) {
                if (response.isSuccessful()) {

                    Headers headerResponse = response.headers();
                    //convert header to Map
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));
                    Toast.makeText(MainActivity.this, "Cookie saved!", Toast.LENGTH_SHORT).show();

                    testCookieAuth();
                } else {
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginUser> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.v(TAG, t.getMessage());
            }
        });
    }

    //TODO move to login view
    private void testCookieAuth(){
        LoginService loginService = ServiceGenerator.createService(LoginService.class);
        Call<LoginUser> cookieCall = loginService.cookieTest();
        cookieCall.enqueue(new Callback<LoginUser>() {
            @Override
            public void onResponse(Call<LoginUser> call, Response<LoginUser> response) {
                Toast.makeText(MainActivity.this, "cookie auth: " + response.message(), Toast.LENGTH_SHORT).show();
                testSavePoi();
            }

            @Override
            public void onFailure(Call<LoginUser> call, Throwable t) {
                Toast.makeText(MainActivity.this, "cookie auth failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void testSavePoi(){
        Poi testPoi = new Poi(0, "testPoi", "des", "path/whatever",
                5.2f, 6.2f, 6, 5, false);
        PoiDao testPoiDao = new PoiDao(boxStore, MainActivity.this);
        testPoiDao.create(testPoi);

//        UserDao testUserDao = new UserDao(boxStore);
//        User testUser = new User(0, "derp", "pipu@popo.miau", "secret",
//                1, false, false, "lastLogin", "acc type");
//        testUserDao.update(testUser, MainActivity.this);

    }

    public void makeToast(String s){
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
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
