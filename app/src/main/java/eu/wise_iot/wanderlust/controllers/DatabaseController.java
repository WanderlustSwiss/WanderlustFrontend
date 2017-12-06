package eu.wise_iot.wanderlust.controllers;


import android.content.Context;
import android.os.AsyncTask;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseObject.CommunityTourDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.DeviceDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.EquipmentDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.HistoryDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.TourKitDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.TripDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import eu.wise_iot.wanderlust.views.DatabaseListener;
import io.objectbox.BoxStore;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Database controller which initializes all Model Dao objects
 * Use this statically to use dao models or boxstore
 * @author Tobias Rüegsegger
 * @license MIT
 */
public final class DatabaseController {

    //true as soon intDaoModels was executed
    public static boolean initialized;

    public static BoxStore boxStore;
    public static CommunityTourDao communityTourDao;
    public static DeviceDao deviceDao;
    public static DifficultyTypeDao difficultyTypeDao;
    public static EquipmentDao equipmentDao;
    public static HistoryDao historyDao;
    public static PoiDao poiDao;
    public static PoiTypeDao poiTypeDao;
    public static ProfileDao profileDao;
    public static TourKitDao tourKitDao;
    public static TripDao tripDao;
    public static UserDao userDao;
    public static UserTourDao userTourDao;

    private static List<DatabaseListener> listeners = new ArrayList<>();
    private static Date lastSync;
    private static boolean syncingPoiTypes;
    private static boolean syncingPois;

    public static void initDaoModels(Context mainContext){

        boxStore = MyObjectBox.builder().androidContext(mainContext).build();
        communityTourDao = new CommunityTourDao();
        deviceDao = new DeviceDao();
        difficultyTypeDao = new DifficultyTypeDao();
        equipmentDao = new EquipmentDao();
        historyDao = new HistoryDao();
        poiDao = new PoiDao();
        poiTypeDao = new PoiTypeDao();
        profileDao = new ProfileDao();
        tourKitDao = new TourKitDao();
        tripDao = new TripDao();
        userDao = new UserDao();
        userTourDao = new UserTourDao();
        initialized = true;
    }

    /**
     * Deletes all files in the frontend database
     */
    public static void flushDatabase(){
        boxStore.deleteAllFiles();
    }

    /**
     * Deletes all pois from the frontend database
     */
    public static void deleteAllPois(){
        poiDao.deleteAll();
    }

    public static void register(DatabaseListener listener){
        listeners.add(listener);
    }

    public static void syncAll(){

        if(!syncingPoiTypes){
            syncingPoiTypes = true;
            poiTypeDao.syncTypes();
        }

        if(!syncingPois){
            syncingPoiTypes = true;
            poiDao.syncPois();
        }
    }

    public static void syncPoiTypesDone(){
        syncingPoiTypes = false;
        sendUpdate(); //TODO hoch komplex, aber mues effizient werde
    }

    public static void syncPoisDone(){
        syncingPois = false;
        sendUpdate();
    }

    public static Date lastSync(){
        return lastSync;
    }
    private static void sendUpdate(){
        for (DatabaseListener listener: listeners) {
            listener.update();
        }
    }

    /**
     * The asynchronous task which handles sync updates
     * Should be running at all times
     */
    class GetImagesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            while(true){

            }

        }
    }
}
