package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.CommunityTour;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
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
public interface CommunityTourService {
    @GET("/CommunityTour/{id}")
        Call<CommunityTour> retrieveCommunityTour(@Path("id") int id);
    @GET("/CommunityTour/")
        Call<CommunityTour> retrieveAllCommunityTours();
    @PUT("/CommunityTour")
        Call<CommunityTour> updateCommunityTour(int id, @Body CommunityTour CommunityTour);
    @DELETE("/CommunityTour/{")
        Call<CommunityTour> deleteCommunityTour(@Body CommunityTour CommunityTour);
}