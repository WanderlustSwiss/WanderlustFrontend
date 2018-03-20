package eu.wise_iot.wanderlust.controllers;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile_;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ProfileService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.views.MainActivity;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static eu.wise_iot.wanderlust.controllers.ImageController.getInstance;

/*
 * Login Controller which handles the login of the user
 * @author Joshua
 * @license MIT
 */
public class LoginController {

    private UserDao userDao;
    private ProfileDao profileDao;
    private DatabaseController databaseController;
    private ImageController imageController;
    /**
     * Create a login contoller
     */
    public LoginController() {
        userDao = UserDao.getInstance();
        profileDao = ProfileDao.getInstance();
        databaseController = DatabaseController.getInstance();
        imageController = ImageController.getInstance();
    }

    public void logIn(LoginUser user, final FragmentHandler handler) {


        DisplayMetrics displayMetrics = new DisplayMetrics();
        MainActivity.activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        String resolution = displayMetrics.widthPixels + "x" + displayMetrics.heightPixels;
        user.setDeviceStatistics(Integer.toString(Build.VERSION.SDK_INT),Build.MODEL, resolution, android.os.Build.SERIAL);

        LoginService service = ServiceGenerator.createService(LoginService.class);
        Call<User> call = service.basicLogin(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Headers headerResponse = response.headers();
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));

                    databaseController.sync(new DatabaseEvent(DatabaseEvent.SyncType.POITYPE));
                    WeatherController.getInstance().initKeys();
                    EquipmentController.getInstance().initEquipment();
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

    private void getProfile(FragmentHandler handler, User user){
        ProfileService service = ServiceGenerator.createService(ProfileService.class);
        Call<Profile> call = service.retrieveProfile();
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                if(response.isSuccessful()){

                    try {
                        Profile internalProfile = profileDao.findOne(Profile_.profile_id, user.getProfile());
                        Profile updatedProfile = response.body();
                        if (internalProfile == null){
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
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), user));
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
    public File getProfileImage(){
        Profile profile = profileDao.getProfile();
        if (profile.getImagePath() != null){
            return imageController.getImage(profile.getImagePath());
        }
        return null;
    }
    public class EmailBody {
        private String email;

        public EmailBody(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }
}

