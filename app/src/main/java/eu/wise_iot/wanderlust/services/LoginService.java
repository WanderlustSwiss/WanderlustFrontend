package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import retrofit2.Call;
import retrofit2.http.POST;

public interface LoginService {
    @POST("/login")
    Call<User> basicLogin();
}
