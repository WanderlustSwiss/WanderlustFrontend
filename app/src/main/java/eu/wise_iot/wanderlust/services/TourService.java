package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.CommunityTours;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * show	    GET	    /trip       | restricted
 * show	    GET	    /trip/:id   | restricted
 * create	POST	/trip       | restricted
 * delete	DELETE	/trip/:id   | restricted
 * @author Alexander Weinbeck
 *
 */
public interface TourService {
    @POST("/tour/")
        Call<CommunityTours> createUser(@Body CommunityTours user);
    @GET("/tour/{id}")
        Call<CommunityTours> retrieveTour(@Path("id") int id);
    @GET("/tour/")
        Call<CommunityTours> retrieveAllTours();
    @PUT("/communityTours")
        Call<CommunityTours> updateTour(int id, @Body CommunityTours communityTours);
    @DELETE("/communityTours/{")
        Call<CommunityTours> deleteTour(@Body CommunityTours communityTours);
}