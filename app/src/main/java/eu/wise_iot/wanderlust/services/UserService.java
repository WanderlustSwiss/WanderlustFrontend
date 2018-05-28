package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * UserService:
 * AuthController
 * register
 * UserController
 * create   POST    /auth/register | restricted
 * show 	GET	    /user | restricted
 * update	PUT	    /user | restricted
 * disable	DELETE	/user | restricted
 *
 * @author Alexander Weinbeck
 */
public interface UserService {
    @POST("auth/register")
    Call<User> createUser(@Body User user);

    @GET("user")
    Call<User> retrieveUser();

    @PUT("user")
    Call<User> updateUser(@Body User user);

    @DELETE("user")
    Call<User> deleteUser(@Body User user);
}