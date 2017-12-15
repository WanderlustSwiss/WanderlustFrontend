package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface LoginService {
    @POST("auth/login")
    Call<User> basicLogin(@Body LoginUser user);

    @GET("auth/logout")
    Call<Void> logout();

    @POST("auth/forgottenpassword")
    Call<Void> resetPassword(@Body LoginController.EmailBody body);

    @GET("auth/authtest")
    Call<LoginUser> cookieTest();
}