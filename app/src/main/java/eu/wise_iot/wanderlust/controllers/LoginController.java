package eu.wise_iot.wanderlust.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Login Controller which handles the login of the user
 * @author Joshua
 * @license MIT
 */
public class LoginController {

    /**
     * Create a login contoller
     */
    public LoginController() {
    }

    public void logIn(LoginUser user, final FragmentHandler handler) {

        LoginService service = ServiceGenerator.createService(LoginService.class);
        Call<User> call = service.basicLogin(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Headers headerResponse = response.headers();
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));
                    DatabaseController.sync(new DatabaseEvent(DatabaseEvent.SyncType.POITYPE));


                    DatabaseController.userDao.userBox.removeAll();
                    User newUser = response.body();
                    newUser.setPassword(user.getPassword());
                    newUser.setInternalId(0);
                    DatabaseController.userDao.userBox.put(newUser);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), newUser));
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

