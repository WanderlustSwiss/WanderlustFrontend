package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.LoginController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EquipmentService {

    @GET("equipment/set/basic")
    Call<List<Equipment>> getEquipment();

    @GET("/equipment/{id}/img")
    Call<ResponseBody> downloadImage(@Path("id") long id);
}