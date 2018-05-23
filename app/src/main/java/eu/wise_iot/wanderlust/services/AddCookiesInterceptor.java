package eu.wise_iot.wanderlust.services;


import java.io.IOException;
import java.util.ArrayList;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AddCookiesInterceptor adds the cookie to each request
 *
 * @author Tobias Rüegsegger
 * @license MIT
 */

public class AddCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        ArrayList<String> preferences = LoginUser.getCookies();

        if (preferences == null) preferences = new ArrayList<>();
        for (String cookie : preferences) {
            builder.addHeader("Cookie", cookie);
        }

        return chain.proceed(builder.build());
    }
}