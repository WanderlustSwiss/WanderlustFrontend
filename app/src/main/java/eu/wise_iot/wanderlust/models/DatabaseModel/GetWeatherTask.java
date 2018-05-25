package eu.wise_iot.wanderlust.models.DatabaseModel;


import android.os.AsyncTask;

import org.joda.time.DateTime;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.WeatherController;

public class GetWeatherTask extends AsyncTask<List<GeoPoint>, Void, List<Weather>> {

    private final FragmentHandler handler;
    private final WeatherController controller;
    private final Weather[] weather;
    private final DateTime dateTime;
    private final long duration;

    public GetWeatherTask(FragmentHandler handler, DateTime dateTime, long duration) {
        this.handler = handler;
        controller = WeatherController.getInstance();
        weather = new Weather[5];
        this.dateTime = dateTime;
        this.duration = duration;
    }

    @Override
    protected List<Weather> doInBackground(List<GeoPoint>[] lists) {

        List<GeoPoint> geoPoints = lists[0];
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new WeatherThread(geoPoints.get(i), controller, weather, dateTime.getMillis()/1000 + (duration / 4) * i, i));
            threads[i].setDaemon(true);
            threads[i].start();
        }


        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        ArrayList<Weather> weatherList = new ArrayList<>(Arrays.asList(weather));
        handler.onResponse(new ControllerEvent<>(EventType.OK, weatherList));
        return weatherList;
    }


    class WeatherThread implements Runnable {

        private final GeoPoint geoPoint;
        private final WeatherController controller;
        private final Weather[] weatherList;
        private final long DTtime;
        private Weather weather;
        private final int index;

        WeatherThread(GeoPoint geoPoint, WeatherController controller, Weather[] weather,
                      long DTtime, int index) {
            this.geoPoint = geoPoint;
            this.controller = controller;
            weatherList = weather;
            this.DTtime = DTtime;
            this.index = index;
        }

        @Override
        public void run() {
            List<Weather> weatherFromGeo = controller.getWeatherFromGeoPoint(geoPoint);
            if (weatherFromGeo != null) {
                long delta = Long.MAX_VALUE;
                for (Weather w : weatherFromGeo) {
                    if (Math.abs(w.getDt() - DTtime) < delta) {
                        delta = Math.abs(w.getDt() - DTtime);
                        weather = w;
                    }
                }
                weatherList[index] = weather;
            }
        }
    }
}
