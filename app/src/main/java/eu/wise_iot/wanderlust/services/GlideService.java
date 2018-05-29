package eu.wise_iot.wanderlust.services;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Glide Module which provides overridden functions to enhance glide usage
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
@GlideModule
public class GlideService extends AppGlideModule {

    /**
     * overriden function to provide following things:
     * connect timeout
     * write timeout
     * cookie interception
     * cookie validation check before rendering
     *
     * @param context {@inheritDoc}
     * @param glide {@inheritDoc}
     * @param registry {@inheritDoc}
     */
    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        if(LoginUser.getCookies() != null && !LoginUser.getCookies().isEmpty() && LoginUser.getCookies().get(0) != null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    //.cache(cache)
                    .addInterceptor(chain -> {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Cookie", LoginUser.getCookies().get(0))
                                .build();

                        return chain.proceed(newRequest);
                    })
                    .build();

            OkHttpUrlLoader.Factory factory = new OkHttpUrlLoader.Factory(client);
            glide.getRegistry().replace(GlideUrl.class, InputStream.class, factory);
        }
    }
}