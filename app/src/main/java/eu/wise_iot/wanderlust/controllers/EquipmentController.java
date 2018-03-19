package eu.wise_iot.wanderlust.controllers;


import android.content.Context;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.SeasonsKeys;
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
}
