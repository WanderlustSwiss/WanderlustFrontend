package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.controllers.TourController;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * provides service for a violation
 *
 * @author Alexander Weinbeck
 */
public interface ViolationService {
    @POST("violation/poi")
    Call<Void> sendPoiViolation(@Body PoiController.Violation violation);
    @POST("violation/tour")
    Call<Void> sendTourViolation(@Body TourController.Violation violation);
}
