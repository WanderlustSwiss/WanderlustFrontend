package eu.wise_iot.wanderlust.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ReceivedCookiesInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            ArrayList<String> cookies = LoginUser.getCookies();

            for (String header : originalResponse.headers("Set-Cookie")) {
                cookies.add(header);
            }

        }

        return originalResponse;
    }
}