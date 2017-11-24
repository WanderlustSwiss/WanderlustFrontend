package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.Feedback;
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
import retrofit2.http.Path;

/**
 * FeedbackService:
 * @author Fabian Schwander
 * @license MIT
 */
public interface FeedbackService {
    @GET("feedbacks")
    Call<List<Feedback>> loadFeedbackList();

    @GET("feedbacks/id/{id}")
    Call<Feedback> loadFeedbackById(@Path("id") long id);

    @POST("feedbacks/save")
    Call<Feedback> saveNewFeedback(@Body Feedback feedback);

    @PUT("feedbacks/update")
    Call<Feedback> updateFeedback(@Body Feedback feedback);

    @DELETE("feedbacks/delete/{id}")
    Call<Feedback> deleteFeedbackById(@Path("id") long id);

    @Multipart
    @POST("upload")
    Call<ResponseBody> uploadPhoto(@Part MultipartBody.Part photo);
}
