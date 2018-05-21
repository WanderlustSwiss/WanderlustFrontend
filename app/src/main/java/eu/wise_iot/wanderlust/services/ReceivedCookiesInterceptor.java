package eu.wise_iot.wanderlust.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * ReceivedCookiesInterceptor saves the cookie from backend
 *
 * @author Tobias Rüegsegger
 * @license MIT
 */

public class ReceivedCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty() && originalResponse.isSuccessful()) {
            ArrayList<String> cookies = LoginUser.getCookies();

            cookies.addAll(originalResponse.headers("Set-Cookie"));

        }

        return originalResponse;
    }
}