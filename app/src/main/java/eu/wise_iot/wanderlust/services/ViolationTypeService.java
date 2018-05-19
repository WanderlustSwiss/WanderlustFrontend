package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 *
 * @author Alexander Weinbeck
 */


public interface ViolationTypeService {
    @GET("violationtype")
    Call<List<ViolationType>> retrieveAllViolationTypes();
}
