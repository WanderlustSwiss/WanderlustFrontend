package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * TripService:
 * TripController
 * show	    GET	    /trip       | restricted
 * show	    GET	    /trip/:id   | restricted
 * create	POST	/trip       | restricted
 * delete	DELETE	/trip/:id   | restricted
 * @author Alexander Weinbeck
 */
public interface TripService {
    @GET("/Trip/")
        Call<Trip> retrieveAllTrips();
    @GET("/Trip/{id}")
        Call<Trip> retrieveTrip(@Path("id") int id);
    @POST("/Trip/")
        Call<Trip> createTrip(@Body Trip user);
   // @PUT("/Trip")
   //     Call<Trip> updateTrip(int id,@Body Trip Trip);
    @DELETE("/Trip/{")
        Call<Trip> deleteTrip(@Body Trip Trip);
}