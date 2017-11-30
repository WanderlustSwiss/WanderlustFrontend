package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * UserTourService:
 * handles exchange with server for Tours
 * server db:
 * show	            GET	        /tour/:id               | restricted
 * show	            GET	        /tour                   | restricted, all public routes
 * update	        PUT	        /tour/:id               | restricted
 * downloadImage	GET	        /tour/:id/img/:image_id | restricted
 * uploadImage	    POST	    /tour/:id/img           | restricted
 * deleteImage	    DELETE	    /tour/:id/img/:image_id | restricted
 * @author Alexander Weinbeck
 */
public interface UserTourService {
    @GET("/tour/{id}")
        Call<UserTour> retrieveUserTour(@Path("id") int id);
    @GET("/tour/")
        Call<UserTour> retrieveAllUserTours();
    @PUT("/tour/{id}")
        Call<UserTour> updateUserTour(int id, @Body UserTour userTour);
    @GET("/poi/{id}/img/{image_id}")
        Call<UserTour> downloadImage(@Path("id") int id, @Path("image_id") int image_id);
    @Multipart
    @POST("/poi/{id}/img")
        Call<Poi> uploadImage(@Path("id") int id, @Part MultipartBody.Part image);
    @DELETE("/tour/{")
        Call<UserTour> deleteUserTour(@Body UserTour userTour);
    @DELETE("/poi/{id}/img/{image_id}")
        Call<UserTour> deleteImage(@Path("id") int id, @Path("image_id") int image_id);
}