package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.GeoObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SacService {

    @GET("geoobject/sac")
    Call<List<GeoObject>> retrieveSacPoisByArea(@Query("lat1") double lat1, @Query("long1") double lon1,
                                                @Query("lat2") double lat2, @Query("long2") double lon2);

}
