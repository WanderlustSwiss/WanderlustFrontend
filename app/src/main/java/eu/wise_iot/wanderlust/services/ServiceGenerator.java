package eu.wise_iot.wanderlust.services;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ServiceGenerator handles all request for the backend database
 * @author Tobias Rüegsegger
 * @license MIT
 */

public class ServiceGenerator {

    //TODO change for production
    /*
     * Defines the URL for the backend communication
     */
    public static final String API_BASE_URL = "http://86.119.40.34:8080/";
    //public static final String API_BASE_URL = "http:///";

    /**
     * Create service for a new backend request
     * @param serviceClass
     * @return service for respective model
     */
    public static <S> S createService(Class<S> serviceClass) {

        OkHttpClient client = new OkHttpClient();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new AddCookiesInterceptor());
        builder.addInterceptor(new ReceivedCookiesInterceptor());
        client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(serviceClass);
    }

}