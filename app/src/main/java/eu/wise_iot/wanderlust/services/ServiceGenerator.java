package eu.wise_iot.wanderlust.services;

import android.text.TextUtils;

import java.util.HashSet;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    public static final String API_BASE_URL = "http://10.0.2.2:1337/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null, null);
    }

    public static <S> S createService(
            Class<S> serviceClass, String username, String password) {



        if (!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)) {

            HashSet<String> testSet = new HashSet<String>();
            testSet.add("FirstCookie");
            LoginUser.setCookies(testSet);

            httpClient.addInterceptor(new AddCookiesInterceptor());
            httpClient.addInterceptor(new ReceivedCookiesInterceptor());


            builder.client(httpClient.build());
            retrofit = builder.build();

            testSet = LoginUser.getCookies();
            return retrofit.create(serviceClass);


            //String authToken = Credentials.basic(username, password);
            //return createService(serviceClass, authToken);
        }

        return createService(serviceClass, null, null);
    }

//    public static <S> S createService(
//            Class<S> serviceClass, final String authToken) {
//        if (!TextUtils.isEmpty(authToken)) {
//
//            httpClient.addInterceptor(new AddCookiesInterceptor());
//            httpClient.addInterceptor(new ReceivedCookiesInterceptor());
//            AuthenticationInterceptor interceptor =
//                    new AuthenticationInterceptor(authToken);
//
//            if (!httpClient.interceptors().contains(interceptor)) {
//                httpClient.addInterceptor(interceptor);
//
//                builder.client(httpClient.build());
//                retrofit = builder.build();
//            }
//        }
//
//        return retrofit.create(serviceClass);
//    }
}