package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * RatingService:
 * RatingController
 *
 * @author Rilind Gashi
 */

public interface RatingService {
    @GET("Rating/tour")
    Call<List<Tour>> retrievAllRatingTours();

    @GET("Rating/{id}")
    Call<Rating> retrieveRating(@Path("id") long id);

    @GET("Rating")
    Call<List<Rating>> retrievAllRatings();

    @POST("Rating")
    Call<Rating> createRating(@Body Tour tour);

    @DELETE("Rating/{id}")
    Call<Rating> deleteRating(@Path("id") long id);
}
