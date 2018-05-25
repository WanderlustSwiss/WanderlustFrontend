package eu.wise_iot.wanderlust.controllers;
import android.content.Context;

import eu.wise_iot.wanderlust.models.DatabaseModel.TourKit;
import eu.wise_iot.wanderlust.models.DatabaseObject.TourKitDao;


public class TourKitController {
    private final TourKitDao tourKitDao;

    private TourKitController(){
        tourKitDao = TourKitDao.getInstance();
    }

    private static class Holder {
        private static final TourKitController INSTANCE = new TourKitController();
    }

    private static Context CONTEXT;

    public static TourKitController createInstance(Context context) {
        CONTEXT = context;
        return TourKitController.Holder.INSTANCE;
    }

    public void addEquipmentToTour(TourKit tourKit, FragmentHandler handler){
        tourKitDao.create(tourKit, handler);
    }
}
