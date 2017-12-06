package eu.wise_iot.wanderlust.controllers;


import android.content.Context;


import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
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
import io.objectbox.BoxStore;

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
}
