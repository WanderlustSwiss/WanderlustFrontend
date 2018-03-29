package eu.wise_iot.wanderlust.services;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * ProfileService:
 * ProfileController
 * show	            GET	    /profile/:id                | restricted
 * update	        PUT	    /profile/:id                | restricted
 * uploadImage	    POST	/profile/:id/img            | restricted
 * deleteImage	    DELETE	/profile/:id/img/:image_id  | restricted
 * downloadImage	GET	    /profile/:id/img/:image_id  | restricted
 *
 * @author Rilind Gashi
 */


public interface ProfileService {
    @GET("profile")
    Call<Profile> retrieveProfile();

    @PUT("profile")
    Call<Profile> updateProfile(@Body Profile profile);

    @Multipart
    @POST("profile/img")
    Call<ImageInfo> uploadImage(@Part MultipartBody.Part image);

    @DELETE("profile/img")
    Call<ImageInfo> deleteImage();

    @GET("profile/img")
    Call<ResponseBody> downloadImage();

}
