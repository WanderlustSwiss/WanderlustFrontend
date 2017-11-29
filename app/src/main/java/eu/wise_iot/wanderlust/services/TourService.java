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
        Call<Tour> create(@Body Tour tour);
    @GET("/tour/{id}")
        Call<Tour> retrieve(@Path("id") int id, @Body User user);
    @GET("/tour/")
        Call<Tour> retrieveAll(@Body Tour tour);
    @PUT("/tour/")
        Call<Tour> update(@Body Tour tour);
    @DELETE("/tour")
        Call<Tour> delete(@Body Tour tour);
}