package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserService {
    @POST("/auth/register")
        Call<User> createUser(@Body User user);
    @GET("/user")
        Call<User> retrieveUser(@Body User user);
    @PUT("/user")
        Call<User> updateUser(@Body User user);
    @DELETE("/user")
        Call<User> deleteUser(@Body User user);
}