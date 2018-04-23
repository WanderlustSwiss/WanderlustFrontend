package eu.wise_iot.wanderlust.controllers;


import android.content.Context;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.models.DatabaseModel.WeatherKeys;
import eu.wise_iot.wanderlust.services.EquipmentService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipmentController {
    private final EquipmentService service;
    private List<Equipment> equipmentList;
    private int typeCount;
    private volatile boolean equipmentInitiated;
    private volatile boolean imagesDownloaded;
    private final ImageController imageController;
    private final WeatherController weatherController;


    private static class Holder {
        private static final EquipmentController INSTANCE = new EquipmentController();
    }

    private static Context CONTEXT;

    public static EquipmentController createInstance(Context context) {
        CONTEXT = context;
        return EquipmentController.Holder.INSTANCE;
    }

    public static EquipmentController getInstance() {
        return CONTEXT != null ? EquipmentController.Holder.INSTANCE : null;
    }

    private EquipmentController() {
        service = ServiceGenerator.createService(EquipmentService.class);
        imageController = ImageController.getInstance();
        weatherController = WeatherController.getInstance();
    }

    public List<Equipment> getEquipmentList() {
        return equipmentInitiated ? equipmentList : new ArrayList<>();
    }

    public int getTypeCount() {
        return typeCount;
    }

    public void initEquipment() {
        Call<List<Equipment>> call = service.getEquipment();
        call.enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful()) {
                    equipmentList = response.body();
                    typeCount = 0;

                    for (Equipment equipment : equipmentList) {
                        if (equipment.getType() > typeCount) {
                            typeCount = equipment.getType();
                        }
                        if(equipment.getImagePath() == null) continue;
                        equipment.getImagePath().setLocalDir(imageController.getEquipmentFolder());
                        Call<ResponseBody> imageCall = service.downloadImage(equipment.getEquip_id());
                        imageCall.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful()) {
                                    try {
                                        imageController.save(response.body().byteStream(), equipment.getImagePath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });


                    }
                    equipmentInitiated = true;
                }
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
            }
        });

    }


    //TODO dateTime seasons
    public void retrieveRecommendedEquipment(Tour tour, DateTime dateTime, FragmentHandler handler) {
        //get weather from points
        weatherController.getWeatherFromTour(tour, dateTime, controllerEvent -> {
            switch (controllerEvent.getType()) {
                case OK:
                    @SuppressWarnings("unchecked")
                    List<Weather> weather = (List<Weather>) controllerEvent.getModel();
                    List<Equipment> equipment = getEquipmentList();


                    //Calculate the score of each weather type
                    float maxTemp = Float.NEGATIVE_INFINITY;
                    float minTemp = Float.POSITIVE_INFINITY;
                    List<WeatherKeys> weatherKeys = weatherController.getWeatherKeys();
                    boolean[] weatherFilter = new boolean[weatherKeys.size()];
                    for (Weather w : weather) {
                        //TODO: Besseres error handling
                        if (w == null){
                            continue;
                        }
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

                    List<Equipment> recEquipmentList = new ArrayList<>();
                    for(Equipment equipmentItem : recommendedEquipment){
                        if(equipmentItem != null) {
                            recEquipmentList.add(equipmentItem);
                        }
                    }
                    handler.onResponse(new ControllerEvent(EventType.OK, recEquipmentList));
                    break;
                default:
            }
        });
    }
}
