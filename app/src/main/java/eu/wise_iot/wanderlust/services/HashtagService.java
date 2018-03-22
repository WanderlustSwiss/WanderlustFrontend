package eu.wise_iot.wanderlust.services;


import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.HashtagResult;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HashtagService {

    @GET("hashtag/search/{tag}")
    Call<List<HashtagResult>> retrievePoisByTag(@Path("tag") String tag);

    @GET("hashtag/poi")
    Call<List<Poi>> retrievePoisByArea(@Query("lat1") double lat1, @Query("long1") double lon1,
                                       @Query("lat2") double lat2, @Query("long2") double lon2, @Query("hash_id") int hash_id);

}

