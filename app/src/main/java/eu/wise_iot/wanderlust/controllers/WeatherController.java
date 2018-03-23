package eu.wise_iot.wanderlust.controllers;

import android.content.Context;
import android.util.Log;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.GetWeatherTask;
import eu.wise_iot.wanderlust.models.DatabaseModel.SeasonsKeys;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
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

    private volatile boolean initKeys = false;

    private static class Holder {
        private static final WeatherController INSTANCE = new WeatherController();
    }

    private static Context CONTEXT;

    public static WeatherController createInstance(Context context) {
        CONTEXT = context;
        return WeatherController.Holder.INSTANCE;
    }

    public static WeatherController getInstance() {
        return CONTEXT != null ? WeatherController.Holder.INSTANCE : null;
    }

    private WeatherController() {
        service = ServiceGenerator.createService(WeatherService.class);
    }


    public List<WeatherKeys> getWeatherKeys() {
        return initKeys ? weatherKeys : null;
    }

    public List<SeasonsKeys> getSeasonsKeys() {
        return initKeys ? seasonsKeys : null;
    }

    public void initKeys() {
        initWeatherKeys(controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    initSeasonsKeys(controllerEvent1 -> {
                        switch (controllerEvent1.getType()) {
                            case OK:
                                initKeys = true;
                                break;
                            default:
                        }
                    });
                    break;
                default:
            }
        });
    }

    private void initWeatherKeys(FragmentHandler handler) {
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

    private void initSeasonsKeys(FragmentHandler handler) {
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


    /**
     * Get weather from tour
     * @param tour
     * @param dateTime
     * @param handler
     */
    public void getWeatherFromTour(Tour tour, DateTime dateTime, FragmentHandler handler) {
        List<GeoPoint> geoPoints = PolyLineEncoder.decode(tour.getPolyline(), 10);
        List<GeoPoint> geoPointsWeather = new ArrayList<>();
        for(int i = 0; i <= 4; i++) {
            int index = ((geoPoints.size() - 1 )/4)*i;
            geoPointsWeather.add(geoPoints.get(index));
        }
        GetWeatherTask weatherTask = new GetWeatherTask(new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                switch (controllerEvent.getType()){
                    case OK:
                        List<Weather> weather = (List<Weather>) controllerEvent.getModel();
                        handler.onResponse(new ControllerEvent(EventType.OK, weather));
                        break;
                    default:
                }

            }
        }, dateTime, tour.getDuration());
        weatherTask.execute(geoPointsWeather);
    }


    /*
    Returns a List of 40 Weathers.
    First weather has current time, second weather has current time + 3hours
    the eights Weather has current time + 1 day
    dt = seconds since unix epoch
    https://stackoverflow.com/questions/9754600/converting-epoch-time-to-date-string
     */
    public void getWeatherFromGeoPoint(GeoPoint geoPoint, FragmentHandler handler) {
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

    /*
    don't use
     */
    public List<Weather> getWeatherFromGeoPoint(GeoPoint geoPoint) {
        Call<List<Weather>> call = service.getWeather(geoPoint.getLatitude(), geoPoint.getLongitude());
        try {
            return call.execute().body();
        } catch (IOException e) {
            return null;
        }
    }
}
