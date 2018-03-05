package eu.wise_iot.wanderlust.models.DatabaseModel;


import android.os.AsyncTask;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImagesTaskParameters;
import eu.wise_iot.wanderlust.controllers.WeatherController;

public class GetWeatherTask extends AsyncTask<List<GeoPoint>, Void, List<Weather>> {

    FragmentHandler handler;
    WeatherController controller;
    List<Weather> weather;

    public GetWeatherTask(WeatherController controller, FragmentHandler handler){
        this.handler = handler;
        this.controller = controller;
        this.weather = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    protected List<Weather> doInBackground(List<GeoPoint>[] lists) {

        List<GeoPoint> geoPoints = lists[0];
        Thread[] threads = new Thread[5];
        for(int i = 0; i < threads.length; i++){
            threads[i] = new Thread(new WeatherThread(geoPoints.get(i),controller, weather));
            threads[i].setDaemon(true);
            threads[i].start();
        }



        for(int i = 0; i < threads.length; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        handler.onResponse(new ControllerEvent<>(EventType.OK, weather));
        return null;
    }


    public class WeatherThread implements Runnable{

        private GeoPoint geoPoint;
        private WeatherController controller;
        private List<Weather> weather;

        public WeatherThread(GeoPoint geoPoint, WeatherController controller, List<Weather> weather){
            this.geoPoint = geoPoint;
            this.controller = controller;
            this.weather = weather;

        }

        @Override
        public void run() {
            //TODO not just get first weather
            weather.add(controller.getWeatherFromGeoPoint(geoPoint).get(0));
        }
    }
}
