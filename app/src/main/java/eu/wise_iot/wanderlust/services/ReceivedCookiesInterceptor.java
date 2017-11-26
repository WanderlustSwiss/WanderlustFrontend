package eu.wise_iot.wanderlust.services;

import java.io.IOException;
import java.util.HashSet;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = new HashSet<>();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

            LoginUser.setCookies(cookies);
        }

        return originalResponse;
    }
}