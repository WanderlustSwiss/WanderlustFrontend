package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserComment;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * UserService:
 * TourController
 * show 	GET	    /comment | restricted
 * create	POST	/comment | restricted
 *
 * @author Simon Kaspar
 */
public interface CommentService {
    @POST("comment/")
    Call<UserComment> createTourComment(@Body UserComment userComment);

    @GET("comment/{tour_id}")
    Call<List<UserComment>> retrieveTourComments(@Path("tour_id") long tour_id, @Query("page") int page);

}