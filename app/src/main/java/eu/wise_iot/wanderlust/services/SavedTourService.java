package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.SavedTour;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * SavedTourService:
 * handles exchange with server for Tours
 * server requests:
 * show	            GET	        /tour/:id               | restricted
 * @author Alexander Weinbeck
 */
public interface SavedTourService {
    @GET("tour/{id}")
    Call<SavedTour> retrieveTour(@Path("id") long id);
}