package eu.wise_iot.wanderlust.services;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ServiceGenerator handles all request for the backend database
 *
 * @author Tobias Rüegsegger, Alexander Weinbeck
 * @license MIT
 */

public class ServiceGenerator {

    /*
     * Defines the URL for the backend communication
     */
    public static final String API_BASE_URL = "https://www.cs.technik.fhnw.ch/wanderlust/";

    private static Retrofit service;

    /**
     * Create service for a new backend request
     *
     * @param serviceClass
     * @return service for respective model
     */
    public static <S> S createService(Class<S> serviceClass) {
        if (service == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                                    .readTimeout(5, TimeUnit.SECONDS)
                                    .writeTimeout(5, TimeUnit.SECONDS)
                                    .connectTimeout(5, TimeUnit.SECONDS)
                                    .addInterceptor(new AddCookiesInterceptor())
                                    .addInterceptor(new ReceivedCookiesInterceptor())
                                    .build();

            service = new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
        }
        return service.create(serviceClass);
    }
}