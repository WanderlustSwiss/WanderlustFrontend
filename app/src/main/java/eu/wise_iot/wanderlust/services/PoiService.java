package eu.wise_iot.wanderlust.services;

import android.accounts.NetworkErrorException;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PoiService {

    @POST("/poi")
    Call<Poi> postPoi(@Body Poi poi);

}