package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * TourService:
 * handles exchange with server for Tours
 * server db:
 * show	            GET	        /tour/:id               | restricted
 * show	            GET	        /tour                   | restricted, all public routes
 * update	        PUT	        /tour/:id               | restricted
 * downloadImage	GET	        /tour/:id/img/:image_id | restricted
 * uploadImage	    POST	    /tour/:id/img           | restricted
 * deleteImage	    DELETE	    /tour/:id/img/:image_id | restricted
 *
 * @author Alexander Weinbeck
 */
public interface TourService {
    @GET("tour/{id}")
    Call<Tour> retrieveTour(@Path("id") long id);

    @GET("tour/")
    Call<List<Tour>> retrieveAllTours(@Query("page") int page);

    @PUT("tour/{id}")
    Call<Tour> updateTour(int id, @Body Tour tour);

    @GET("tour/{id}/img/{image_id}")
    Call<ResponseBody> downloadImage(@Path("id") long id, @Path("image_id") int image_id);

    @Multipart
    @POST("tour/{id}/img")
    Call<ImageInfo> uploadImage(@Path("id") int id, @Part MultipartBody.Part image);

    @DELETE("tour/{")
    Call<Tour> deleteTour(@Body Tour tour);

    @DELETE("tour/{id}/img/{image_id}")
    Call<ImageInfo> deleteImage(@Path("id") int id, @Path("image_id") int image_id);
}