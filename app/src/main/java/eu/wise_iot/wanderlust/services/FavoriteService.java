package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * FavoriteService:
 * FavoriteController
 *
 * @author Rilind Gashi
 */

public interface FavoriteService {
    @GET("favorite/tour")
    Call<List<Tour>> retrievAllFavoriteTours();

    @GET("favorite/{id}")
    Call<Favorite> retrieveFavorite(@Path("id") long id);

    @GET("favorite")
    Call<List<Favorite>> retrievAllFavorites();

    @POST("favorite")
    Call<Favorite> createFavorite(@Body Tour tour);

    @DELETE("favorite/{id}")
    Call<Favorite> deleteFavorite(@Path("id") long id);
}
