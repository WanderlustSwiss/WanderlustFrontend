package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.TourKit;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TourKitService {

    @POST("tourkit")
    Call<TourKit> addEquipmentToTour(@Body TourKit tourKit);
}
