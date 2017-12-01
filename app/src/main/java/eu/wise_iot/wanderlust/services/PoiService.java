package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * PoiService:
 * POIController
 * show	            GET	    /poi/:id                | restricted
 * show	            GET	    /poi                    | restricted
 * create	        POST	/poi                    | restricted
 * update	        PUT	    /poi/:id                | restricted
 * disable	        DELETE	/poi/:id                | restricted
 * uploadImage	    POST	/poi/:id/img            | restricted
 * deleteImage	    DELETE	/poi/:id/img/:image_id  | restricted
 * downloadImage	GET	    /poi/:id/img/:image_id  | restricted
 * @author Alexander Weinbeck
 */
public interface PoiService {
    @GET("/poi/{id}")
        Call<Poi> retrievePoi(@Path("id") int id);
    @GET("/poi/")
        Call<List<Poi>> retrieveAllPois();
    @POST("/poi")
        Call<Poi> createPoi(@Body Poi poi);
    @PUT("/poi/{id}")
        Call<Poi> updatePoi(int id, @Body Poi tour);
    @DELETE("/poi/{id}")
        Call<Poi> deletePoi(@Body Poi tour);
    @Multipart
    @POST("/poi/{id}/img")
        Call<Poi.ImageInfo> uploadImage(@Path("id") int id, @Part MultipartBody.Part image);
    @DELETE("/poi/{id}/img/{image_id}")
        Call<Poi.ImageInfo> deleteImage(@Path("id") int id, @Path("image_id") int image_id);
    @GET("/poi/{id}/img/{image_id}")
        Call<Poi.ImageInfo> downloadImage(@Path("id") int id, @Path("image_id") int image_id);
}