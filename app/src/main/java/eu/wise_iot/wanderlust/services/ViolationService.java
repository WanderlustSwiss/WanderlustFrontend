package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.controllers.TourController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * ProfileService:
 * ProfileController
 * show	            GET	    /profile/:id                | restricted
 * update	        PUT	    /profile/:id                | restricted
 * uploadImage	    POST	/profile/:id/img            | restricted
 * deleteImage	    DELETE	/profile/:id/img/:image_id  | restricted
 * downloadImage	GET	    /profile/:id/img/:image_id  | restricted
 *
 * @author Alexander Weinbeck
 */


public interface ViolationService {
    @POST("violation/poi")
    Call<PoiController.Violation> sendPoiViolation();
    @POST("violation/tour")
    Call<TourController.Violation> sendTourViolation();
}
