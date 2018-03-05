package eu.wise_iot.wanderlust.controllers;

import android.content.Context;

import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.SeasonsKeys;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.models.DatabaseModel.WeatherKeys;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherController {

    private WeatherService service;
    private List<WeatherKeys> weatherKeys;
    private List<SeasonsKeys> seasonsKeys;

    private static class Holder {
        private static final WeatherController INSTANCE = new WeatherController();
    }

    private static Context CONTEXT;

    public static WeatherController createInstance(Context context){
        CONTEXT = context;
        return WeatherController.Holder.INSTANCE;
    }

    public static WeatherController getInstance(){
        return CONTEXT != null ? WeatherController.Holder.INSTANCE : null;
    }

    private WeatherController(){
        service = ServiceGenerator.createService(WeatherService.class);
    }


    public List<WeatherKeys> getWeatherKeys() {
        return weatherKeys;
    }

    public List<SeasonsKeys> getSeasonsKeys() {
        return seasonsKeys;
    }

    public void initWeatherKeys(FragmentHandler handler){
        Call<List<WeatherKeys>> call = service.getWeatherKeys();
        call.enqueue(new Callback<List<WeatherKeys>>() {
            @Override
            public void onResponse(Call<List<WeatherKeys>> call, Response<List<WeatherKeys>> response) {
                if (response.isSuccessful()) {
                    weatherKeys = response.body();
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<List<WeatherKeys>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void initSeaonsKeys(FragmentHandler handler){
        Call<List<SeasonsKeys>> call = service.getSeasonsKeys();
        call.enqueue(new Callback<List<SeasonsKeys>>() {
            @Override
            public void onResponse(Call<List<SeasonsKeys>> call, Response<List<SeasonsKeys>> response) {
                if (response.isSuccessful()) {
                    seasonsKeys = response.body();
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<List<SeasonsKeys>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void getWeatherFromGeoPoint(GeoPoint geoPoint, FragmentHandler handler){
        Call<List<Weather>> call = service.getWeather(geoPoint.getLatitude(), geoPoint.getLongitude());
        call.enqueue(new Callback<List<Weather>>() {
            @Override
            public void onResponse(Call<List<Weather>> call, Response<List<Weather>> response) {
                if (response.isSuccessful()) {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<List<Weather>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public List<Weather> getWeatherFromGeoPoint(GeoPoint geoPoint){
        Call<List<Weather>> call = service.getWeather(geoPoint.getLatitude(), geoPoint.getLongitude());
        try {
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }
}
