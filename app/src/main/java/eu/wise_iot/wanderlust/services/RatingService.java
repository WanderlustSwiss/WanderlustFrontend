package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourRate;
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

    @GET("rating/tour/{tourId}")
    Call<TourRate> retrieveTourRating(@Path("tourId") long tourId);

    @GET("rating/{id}")
    Call<Rating> retrieveRating(@Path("id") long id);

    @POST("rating")
    Call<Rating> createRating(@Body Rating rating);

    @DELETE("rating/{id}")
    Call<Rating> deleteRating(@Path("id") long id);
}
