package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TourService {
    @POST("/auth/register")
        Call<Tour> createTour(@Body Tour tour);
    @GET("/tour/{id}")
        Call<Tour> retrieveTour(@Path("id") int id);
    @GET("/tour/")
        Call<Tour> retrieveAllTours();
    @PUT("/tour")
        Call<Tour> updateTour(@Body Tour tour);
    @DELETE("/tour")
        Call<Tour> deleteTour(@Body Tour tour);
}