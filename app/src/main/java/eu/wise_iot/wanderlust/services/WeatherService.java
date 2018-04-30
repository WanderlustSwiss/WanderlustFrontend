package eu.wise_iot.wanderlust.services;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.SeasonsKey;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.models.DatabaseModel.WeatherKeys;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherService {


    @GET("weather")
    Call<List<Weather>> getWeather(@Query("latitude") double lat, @Query("longitude") double lon);

    @GET("weather/keys")
    Call<List<WeatherKeys>> getWeatherKeys();

    @GET("weather/seasons")
    Call<List<SeasonsKey>> getSeasonsKeys();

}
