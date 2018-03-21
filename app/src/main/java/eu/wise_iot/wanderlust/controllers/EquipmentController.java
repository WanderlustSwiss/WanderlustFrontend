package eu.wise_iot.wanderlust.controllers;


import android.content.Context;

import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.SeasonsKeys;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.models.DatabaseModel.WeatherKeys;
import eu.wise_iot.wanderlust.services.EquipmentService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.WeatherService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipmentController {
    private EquipmentService service;
    private List<Equipment> equipmentList;
    private int typeCount;

    private static class Holder {
        private static final EquipmentController INSTANCE = new EquipmentController();
    }

    private static Context CONTEXT;

    public static EquipmentController createInstance(Context context){
        CONTEXT = context;

        return EquipmentController.Holder.INSTANCE;
    }

    public static EquipmentController getInstance(){
        return CONTEXT != null ? EquipmentController.Holder.INSTANCE : null;
    }

    private EquipmentController(){
        service = ServiceGenerator.createService(EquipmentService.class);
    }

    //TODO safety -> boolean is initiatet
    public List<Equipment> getEquipmentList(){
        return equipmentList;
    }

    public int getTypeCount() {
        return typeCount;
    }

    public void initEquipment(){
        Call<List<Equipment>> call = service.getEquipment();
        call.enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful()) {
                    equipmentList = response.body();
                    typeCount = 0;
                    for(Equipment equipment : equipmentList){
                            if(equipment.getType() > typeCount){
                                typeCount = equipment.getType();
                        }
                    }}
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
            }
        });

    }

    /**
     *
     * @param tour
     * @param handler
     */
    public void retrieveRecommendedEquipment(Tour tour, FragmentHandler handler) {


        WeatherController weatherController = WeatherController.getInstance();

        //get weather from points
        weatherController.getWeatherFromTour(tour, new DateTime(), new FragmentHandler() {
            @Override
            public void onResponse(ControllerEvent controllerEvent) {
                switch (controllerEvent.getType()) {
                    case OK:
                        List<Weather> weather = (List<Weather>) controllerEvent.getModel();
                        List<Equipment> equipment = getEquipmentList();


                        //Calculate the score of each weather type
                        float maxTemp = Float.NEGATIVE_INFINITY;
                        float minTemp = Float.POSITIVE_INFINITY;
                        List<WeatherKeys> weatherKeys = weatherController.getWeatherKeys();
                        boolean[] weatherFilter = new boolean[weatherKeys.size()];
                        for (Weather w : weather) {

                            weatherFilter[w.getCategory()] = true;

                            if (w.getMaxTemp() > maxTemp) {
                                maxTemp = w.getMaxTemp();
                            }
                            if (w.getMinTemp() < minTemp) {
                                minTemp = w.getMinTemp();
                            }

                        }


                        //safe equipment at array pos = type of equipment
                        Equipment[] recommendedEquipment = new Equipment[getTypeCount()];

                        for (Equipment e : equipment) {

                            //If Equipment is not in recommended temperature skip it
                            if (e.getMaxTemperature() < maxTemp || e.getMinTemperature() > minTemp) {
                                continue;
                            }


                            //Check if type of equipment is already present
                            if (recommendedEquipment[e.getType() - 1] != null) {

                                //Check if better than current recommended
                                Equipment current = recommendedEquipment[e.getType() - 1];

                                int scoreCurrent = 0;
                                int scoreNew = 0;

                                //Calculate score from weather
                                for (int i = 0; i < current.getWeather().length; i++) {
                                    if (current.getWeather()[i] == 1 && weatherFilter[i]) {
                                        scoreCurrent++;
                                    }
                                }

                                for (int i = 0; i < e.getWeather().length; i++) {
                                    if (e.getWeather()[i] == 1 && weatherFilter[i]) {
                                        scoreNew++;
                                    }
                                }

                                //set Equipment e to the recommended if it is better suited
                                if (scoreNew > scoreCurrent) {
                                    recommendedEquipment[e.getType() - 1] = e;
                                }
                            }
                            //Else set it recommended
                            else {

                                //If at least one weather type
                                for (int i = 0; i < e.getWeather().length; i++) {
                                    if (e.getWeather()[i] == 1 && weatherFilter[i]) {
                                        recommendedEquipment[e.getType() - 1] = e;
                                        break;
                                    }
                                }

                            }
                        }


                        List<Equipment> recEquipmentList = Arrays.asList(recommendedEquipment);
                        handler.onResponse(new ControllerEvent(EventType.OK, recEquipmentList));
                        break;
                    default:

                }
            }
        });
    }
}
