package eu.wise_iot.wanderlust.controllers;


import android.content.Context;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourKitEquipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Weather;
import eu.wise_iot.wanderlust.models.DatabaseModel.WeatherKeys;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import eu.wise_iot.wanderlust.services.EquipmentService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipmentController {
    private final EquipmentService service;
    private List<Equipment> equipmentList;
    private List<Equipment> extraEquipmentList;

    private volatile int typeCount;
    private volatile boolean equipmentInitiated;
    private volatile boolean extraEquipmentInitiated = false;
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

    public EquipmentController() {
        service = ServiceGenerator.createService(EquipmentService.class);
        imageController = ImageController.getInstance();
        weatherController = WeatherController.getInstance();
    }

    public List<Equipment> getEquipmentList() {
        return equipmentInitiated ? equipmentList : new ArrayList<>();
    }

    public List<Equipment> getExtraEquipmentList() {
        return extraEquipmentInitiated ? extraEquipmentList : new ArrayList<>();
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

    public void initExtraEquipment() {
        Call<List<Equipment>> call = service.getExtraEquipment();
        call.enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful()) {
                    extraEquipmentList = response.body();

                    if(extraEquipmentList == null)
                        return;

                    for (Equipment equipment : extraEquipmentList) {
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
                    extraEquipmentInitiated = true;
                }
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
                int x = 3;
            }
        });

    }

    public void getExtraEquipment(long id, FragmentHandler handler){
        UserTourDao.getInstance().getExtraEquipment(id, handler);
    }

    public void retrieveRecommendedEquipment(Tour tour, DateTime dateTime, FragmentHandler handler) {
        //get weather from points
        weatherController.getWeatherFromTour(tour, dateTime, (ControllerEvent controllerEvent) -> {
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
                    ArrayList<Equipment> basicEquipment = new ArrayList<>();


                    //find out current Season
                    int currentSeasonKey = weatherController.getCurrentSeason();


                    for (Equipment e : equipment) {

                        //Add to basic equipment
                        if(e.getType() == 1){
                            basicEquipment.add(e);
                            continue;
                        }

                        //If Equipment is not in recommended temperature skip it
                        if (e.getMaxTemperature() < maxTemp || e.getMinTemperature() > minTemp)  {
                            continue;
                        }

                        //If Equipment is not in current season skip it
                        byte[] seasonsEquipment = e.getSeasons();
                        boolean seasonIsContained = false;
                        for(int i = 0; i < seasonsEquipment.length; i++){
                            if((seasonsEquipment[i] == 1) && currentSeasonKey == i){
                                seasonIsContained = true;
                                break;
                            }
                        }

                        if(!seasonIsContained){
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

                    //Remove null type equipments
                    List<Equipment> recEquipmentList = new ArrayList<>();
                    for(Equipment equipmentItem : recommendedEquipment){
                        if(equipmentItem != null) {
                            recEquipmentList.add(equipmentItem);
                        }
                    }

                    //Add basic equipment
                    recEquipmentList.addAll(basicEquipment);

                    //Add extra equipment
                    getExtraEquipment(tour.getTour_id(), controllerEventExtraEquipment -> {
                        switch (controllerEventExtraEquipment.getType()){
                            case OK:
                                List<TourKitEquipment> extraEquipment = (List<TourKitEquipment>) controllerEventExtraEquipment.getModel();
                                List<Equipment> extraEquipmentLocal = getExtraEquipmentList();


                                for(TourKitEquipment tourKitEquipment : extraEquipment){
                                    long id = tourKitEquipment.getEquipment().getEquip_id();
                                    for(Equipment equipmentExtra : extraEquipmentLocal){
                                        if(equipmentExtra.getEquip_id() == id){
                                            recEquipmentList.add(equipmentExtra);
                                            break;
                                        }
                                    }
                                }
                                handler.onResponse(new ControllerEvent(EventType.OK, recEquipmentList));
                                break;
                            case NOT_FOUND:
                                //No extra Equipment
                                handler.onResponse(new ControllerEvent(EventType.OK, recEquipmentList));
                            default:
                                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
                        }
                    });
                    break;
                default:
            }
        });
    }
}
