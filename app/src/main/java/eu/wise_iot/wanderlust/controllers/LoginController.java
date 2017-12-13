package eu.wise_iot.wanderlust.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.views.LoginFragment;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Login Controller which handles the login of the user
 * @author Joshua
 * @license MIT
 */
public class LoginController {
    private LoginFragment loginFragment;

    /**
     * Create a login contoller
     */
    public LoginController(LoginFragment fragment){
        this.loginFragment = fragment;
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

                    //TODO add user to database
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR, null));
            }
        });
    }

    public void resetPassword(String email, final FragmentHandler handler) {

        LoginService service = ServiceGenerator.createService(LoginService.class);
        Call<Void> call = service.resetPassword(new EmailBody(email));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Event event = new Event(EventType.getTypeByCode(response.code()), null);
                    handler.onResponse(event);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR, null));
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

