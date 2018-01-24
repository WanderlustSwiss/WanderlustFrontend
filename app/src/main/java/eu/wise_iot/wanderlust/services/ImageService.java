package eu.wise_iot.wanderlust.services;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ImageService {

    @GET("{route}/{id}/img/{image_id}")
    Call<ResponseBody> downloadImage(@Path("route") String route, @Path("id") long id, @Path("image_id") long image_id);
}
