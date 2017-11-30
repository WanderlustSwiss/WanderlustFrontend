package eu.wise_iot.wanderlust.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.views.LoginFragment;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController {
    private LoginFragment loginFragment;

    public LoginController(LoginFragment fragment){
        this.loginFragment = fragment;
    }

    public void logIn(LoginUser user, final FragmentHandler handler){

        LoginService service = ServiceGenerator.createService(LoginService.class);
        Call<User> call = service.basicLogin(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    Headers headerResponse = response.headers();
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else{
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),null));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }

}
