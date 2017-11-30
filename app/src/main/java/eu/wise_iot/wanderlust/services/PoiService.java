package eu.wise_iot.wanderlust.services;

import android.accounts.NetworkErrorException;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PoiService {

    @POST("/poi")
    Call<Poi> createPoi(@Body Poi poi);

    @Multipart
    @POST("/poi/{id}/img")
    Call<Poi> uploadImage(@Path("id") int id, @Part MultipartBody.Part image);

}