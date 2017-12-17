package eu.wise_iot.wanderlust.controllers;


import android.content.Context;
import android.util.Log;


import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
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
import eu.wise_iot.wanderlust.views.MainActivity;
import io.objectbox.BoxStore;

/**
 * Database controller which initializes all Model Dao objects
 * Use this statically to use dao models or boxstore
 *
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

    public static Context mainContext;

    private static List<DatabaseListener> listeners = new ArrayList<>();
    private static Date lastSync;
    private static boolean syncingPoiTypes;
    private static boolean syncingPois;

    private static LinkedList<DownloadedImage> downloadedImages = new LinkedList<>();
    private static long cacheSize;

    //in bytes
    private static final long MAXCACHESIZE = 20_000_000;

    public static void initDaoModels(Context context) {

        if (!initialized) {
            boxStore = MyObjectBox.builder().androidContext(context).build();
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
            mainContext = context;
            initialized = true;
        }
    }

    /**
     * Deletes all files in the frontend database
     */
    public static void flushDatabase() {
        boxStore.deleteAllFiles();
    }

    /**
     * Deletes all pois from the frontend database
     */
    public static void deleteAllPois() {
        poiDao.deleteAll();
    }


    /**
     * syncs models based on syncType
     * @param event
     */
    public static void sync(DatabaseEvent event) {

        lastSync = new Date();
        switch (event.getType()){
            case POI:
                //TODO no longer used?
                if (!syncingPois) {
                    syncingPois = true;
                    poiDao.syncPois();
                }
                break;
            case POITYPE:
                if (!syncingPoiTypes) {
                    syncingPoiTypes = true;
                    poiTypeDao.syncTypes();
                }
                break;
            case POIAREA:
                if (!syncingPois) {
                    syncingPois = true;
                    BoundingBox box = (BoundingBox) event.getObj();
                    poiDao.syncPois(box);
                }
                break;
            default:
                sendUpdate(event);
        }
    }


    public static void syncPoiTypesDone() {
        syncingPoiTypes = false;
        sendUpdate(new DatabaseEvent(DatabaseEvent.SyncType.POITYPE));
    }

    public static void syncPoisDone() {
        syncingPois = false;
        sendUpdate(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA));
    }

    /**
     * @return the time when the last sync !STARTED!
     */
    public static Date lastSync() {
        return lastSync;
    }


    public static void addDownloadedImages(List<DownloadedImage> images){

        for (DownloadedImage image : images){
            downloadedImages.add(image);
            cacheSize += image.getSize();
        }
        clearCache();
    }

    public static void clearCache(){

        //TODO endless loop if userimages are higher than maxchachesize
        while(cacheSize >= MAXCACHESIZE){
            DownloadedImage image = downloadedImages.getFirst();
            if(image.isPublic()) {
                if (image.getImage().delete()) {
                    cacheSize -= image.getSize();
                    downloadedImages.remove(image);
                } else {
                    Log.e(DatabaseController.class.toString(),
                            "image " + image.getImage().getAbsolutePath() + " could not be deleted");
                    break;
                }
            }
        }

    }


    /**
     * Deletes all .jpg files in the app storage
     */
    public static void clearAllDownloadedImages(){
        List<Long> privatePoiIds = new ArrayList<>();
        for(Poi poi : poiDao.find(Poi_.isPublic, false)){
            privatePoiIds.add(poi.getPoi_id());
        }
        File filesDir = mainContext.getApplicationContext().getFilesDir();

        for(File image : filesDir.listFiles()){
            String name = image.getName();
            int dotIndex = name.lastIndexOf('.');
            if(dotIndex == -1) continue; //not a valid file
            String extension = name.substring(dotIndex+1);
            try{
                long imageId = Long.parseLong(name.substring(name.indexOf('-')+1, name.indexOf('.')));
                if(privatePoiIds.contains(imageId) && extension.equals("jpg")) {
                    if (!image.delete()) {
                        Log.e(DatabaseController.class.toString(),
                                "image " + image.getAbsolutePath() + " could not be deleted");
                        break;
                    }
                }
            }catch (NumberFormatException e){
                continue;
            }
        }
        cacheSize = 0;
    }

    public static void deletePoiImages(Poi poi){
        byte[] images = poi.getImageIds();
        for(int i = 0; i < poi.getImageCount(); i++){
            File image = poi.getImageById(images[i]);
            if(!image.delete()){
                Log.e(DatabaseController.class.toString(),
                        "image " + image.getAbsolutePath() + " could not be deleted");
            }
        }
    }

    public static void sendUpdate(DatabaseEvent event) {
        for (DatabaseListener listener : listeners) {
            listener.update(event);
        }
    }

    public static void register(DatabaseListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void unregister(DatabaseListener listener) {
        listeners.remove(listener);
    }
}
