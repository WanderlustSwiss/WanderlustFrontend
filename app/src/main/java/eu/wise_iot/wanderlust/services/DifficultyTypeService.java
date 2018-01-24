package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType;
import retrofit2.Call;
import retrofit2.http.GET;


/**
 * DifficultyTypeService:
 * DifficultyTypeController
 * show	            GET	    /difficultytype         | restricted
 *
 * @author Alexander Weinbeck
 */
public interface DifficultyTypeService {
    @GET("difficultytype")
    Call<List<DifficultyType>> retrieveAllDifficultyTypes();
}