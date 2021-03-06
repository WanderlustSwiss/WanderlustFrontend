package eu.wise_iot.wanderlust.controllers;

import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile_;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RecentTourDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.RegionDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ViolationTypeDao;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ProfileService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.UserService;
import eu.wise_iot.wanderlust.views.MainActivity;
import okhttp3.Headers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Login Controller which handles the login of the user
 * @author Joshua
 * @license MIT
 */
public class LoginController {

    private final UserDao userDao;
    private final ProfileDao profileDao;
    private final DatabaseController databaseController;
    private final ImageController imageController;
    private final WeatherController weatherController;
    private final EquipmentController equipmentController;
    private final RegionDao regionDao;
    private final DifficultyTypeDao difficultyTypeDao;
    private final ViolationTypeDao violationTypeDao;
    private final FavoriteDao favoriteDao;
    private final PoiDao poiDao;
    private final RecentTourDao recentTourDao;
    /**
     * Create a login contoller
     */
    public LoginController() {
        userDao = UserDao.getInstance();
        profileDao = ProfileDao.getInstance();
        databaseController = DatabaseController.getInstance();
        imageController = ImageController.getInstance();
        weatherController = WeatherController.getInstance();
        equipmentController = EquipmentController.getInstance();
        regionDao = RegionDao.getInstance();
        difficultyTypeDao = DifficultyTypeDao.getInstance();
        violationTypeDao = ViolationTypeDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        poiDao = PoiDao.getInstance();
        recentTourDao = RecentTourDao.getInstance();
    }
    /**
     * perform login
     *
     * @param user to log in
     * @return ControllerEvent from async retrofit call
     */
    public void logIn(LoginUser user, final FragmentHandler handler) {
        setDeviceInfo(user);

        if (BuildConfig.DEBUG) Log.d("login", "login: logging in with retro");
        LoginService service = ServiceGenerator.createService(LoginService.class);
        Call<User> call = service.basicLogin(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (BuildConfig.DEBUG) Log.d("response arrived", "getting profile start");
                if (response.isSuccessful() && !LoginUser.getCookies().isEmpty()) {
                    Headers headerResponse = response.headers();
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));
                    User updatedUser = response.body();
                    User internalUser = userDao.getUser();
                    if (internalUser == null){
                        updatedUser.setInternalId(0);
                        UserDao.getInstance().removeAll();
                    }else{
                        updatedUser.setInternalId(internalUser.getInternalId());
                    }
                    updatedUser.setPassword(user.getPassword());
                    userDao.update(updatedUser);
                    if (BuildConfig.DEBUG) Log.d("init data", "getting profile start");
                    initAppData();
                    if (BuildConfig.DEBUG) Log.d("getting profile", "getting profile start");
                    getProfile(handler, updatedUser);
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }

        });
    }

    /**
     * perform a sequential login for asynctask
     *
     * @param user to log in
     * @return ControllerEvent from async retrofit call
     */
    public ControllerEvent logInSequential(LoginUser user){
        final AtomicReference<ControllerEvent> event = new AtomicReference<>();
        try {
            CountDownLatch countDownLatchThread = new CountDownLatch(1);
            logIn(user, controllerEvent -> {
                event.set(controllerEvent);
                countDownLatchThread.countDown();
            });
            countDownLatchThread.await();
            return event.get();
        } catch (Exception e){
            if (BuildConfig.DEBUG) Log.d("loginController","failure while processing request");
        }
        return event.get();
    }

    public void logInWithExternalProvider(String cookie, final FragmentHandler handler){
        ArrayList<String> cookieList = new ArrayList<>();
        cookieList.add(cookie);
        LoginUser.setCookies(cookieList);
        if (BuildConfig.DEBUG) Log.d("COOKIE", cookie);

        UserService service = ServiceGenerator.createService(UserService.class);
        Call<User> call = service.retrieveUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    User updatedUser = response.body();
                    User internalUser = userDao.getUser();
                    if (internalUser == null){
                        updatedUser.setInternalId(0);
                        UserDao.getInstance().removeAll();
                    }else{
                        updatedUser.setInternalId(internalUser.getInternalId());
                    }
                    userDao.update(updatedUser);
                    initAppData();
                    getProfile(handler, updatedUser);
                }else{
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });

    }
    private void getProfile(FragmentHandler handler, User user){
        ProfileService service = ServiceGenerator.createService(ProfileService.class);
        Call<Profile> call = service.retrieveProfile();
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){
                    Profile internalProfile = profileDao.findOne(Profile_.profile_id, user.getProfile());
                    Profile updatedProfile = response.body();
                    if (internalProfile == null){
                        if(updatedProfile.getImagePath() != null)
                            updatedProfile.getImagePath().setLocalDir(imageController.getProfileFolder());

                        profileDao.removeAll();
                        updatedProfile.setInternal_id(0);
                        profileDao.create(updatedProfile);
                    }else{
                        if (updatedProfile.getImagePath() != null){
                            updatedProfile.getImagePath().setLocalDir(imageController.getProfileFolder());
                        }
                        updatedProfile.setInternal_id(internalProfile.getInternal_id());
                        profileDao.update(updatedProfile);
                    }
                    downloadProfileImage(updatedProfile, user, handler);
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void downloadProfileImage(Profile profile, User user, FragmentHandler handler) {
        ProfileService service = ServiceGenerator.createService(ProfileService.class);
        Call<ResponseBody> imageCall = service.downloadImage();
        imageCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        imageController.save(response.body().byteStream(), profile.getImagePath());
                        profile.getImagePath().setLocalDir(imageController.getProfileFolder());
                        profileDao.update(profile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), user));
                } else {
                    if(EventType.getTypeByCode(response.code()) == EventType.NOT_FOUND){
                        //redirect even no image found
                        handler.onResponse(new ControllerEvent(EventType.OK, user));
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
    public User getAvailableUser(){
        return userDao.getUser();
    }

    public void logout(FragmentHandler handler) {
        LoginService service = ServiceGenerator.createService(LoginService.class);
        Call<Void> call = service.logout();
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void resetPassword(String email, final FragmentHandler handler) {
        LoginService service = ServiceGenerator.createService(LoginService.class);
        Call<Void> call = service.resetPassword(new EmailBody(email));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                ControllerEvent event = new ControllerEvent(EventType.getTypeByCode(response.code()));
                handler.onResponse(event);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }


    public ControllerEvent resetPasswordSequential(String email){
        final AtomicReference<ControllerEvent> event = new AtomicReference<>();
        try {
            CountDownLatch countDownLatchThread = new CountDownLatch(1);
            resetPassword(email, controllerEvent -> {
                event.set(controllerEvent);
                countDownLatchThread.countDown();
            });
            countDownLatchThread.await();
            return event.get();
        } catch (Exception e){
            if (BuildConfig.DEBUG) Log.d("loginController","failure while processing request");
        }
        return event.get();
    }

    public File getProfileImage(){
        Profile profile = profileDao.getProfile();
        if (profile != null && profile.getImagePath() != null){
            return imageController.getImage(profile.getImagePath());
        }
        return null;
    }
    public class EmailBody {
        private final String email;

        public EmailBody(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    /**
     * This Method is used to download all app data like (equipment, poi types etc.) when login is performed
     */
    private void initAppData(){
        databaseController.sync(new DatabaseEvent(DatabaseEvent.SyncType.POITYPE));
        weatherController.initKeys();
        equipmentController.initEquipment();
        regionDao.retrieveAll();
        difficultyTypeDao.retrieve();
        violationTypeDao.retrieveAllViolationTypes();
        favoriteDao.retrieveAllFavorites();
        poiDao.removeNonUserPois(userDao.getUser().getUser_id());
        poiDao.retrieveUserPois();
        equipmentController.initExtraEquipment();
        recentTourDao.updateRecentToursOnStartup();
    }

    public void setDeviceInfo(LoginUser user){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        MainActivity.activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        String resolution = displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
        user.setDeviceStatistics(Integer.toString(Build.VERSION.SDK_INT),Build.MODEL, resolution, android.os.Build.SERIAL);
    }
}

