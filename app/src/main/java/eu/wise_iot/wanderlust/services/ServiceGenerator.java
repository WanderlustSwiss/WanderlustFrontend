package eu.wise_iot.wanderlust.services;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ServiceGenerator handles all request for the backend database
 *
 * @author Tobias Rüegsegger
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */

public class ServiceGenerator {

    /**
     * Defines the URL for the backend communication remote server
     */
    //productive host
    //public static final String API_BASE_URL = "https://www.cs.technik.fhnw.ch/wanderlust/";
    //local development hosts (recommended)
    //public static final String API_BASE_URL = "http://10.0.2.2:1337";
    //public static final String API_BASE_URL = "http://192.168.56.1";
    //public static final String API_BASE_URL = "http://192.168.178.61:1337";
    //build host
    public static final String API_BASE_URL = "http://86.119.40.34:8080";

    private static Retrofit service;
    private static OkHttpClient client;

    /**
     * Create service for a new backend request
     * if already set there will be not a new instance
     * this prevents threads flooding
     *
     * @param serviceClass served class
     * @return service for respective model
     */
    public static <S> S createService(Class<S> serviceClass) {
        if (client == null) {

            client = new OkHttpClient.Builder()
                                    .readTimeout(5, TimeUnit.SECONDS)
                                    .writeTimeout(5, TimeUnit.SECONDS)
                                    .connectTimeout(5, TimeUnit.SECONDS)
                                    .addInterceptor(new AddCookiesInterceptor())
                                    .addInterceptor(new ReceivedCookiesInterceptor())
                                    .build();
        }
        service = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return service.create(serviceClass);
    }
}