package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Region;
import retrofit2.Call;
import retrofit2.http.GET;


/**
 * RegionService:
 * TourController
 * show	            GET	         /region         | restricted
 *
 * @author SimonKaspar
 */
public interface RegionService {
    @GET("region")
    Call<List<Region>> retrieveAllRegions();
}