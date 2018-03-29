package eu.wise_iot.wanderlust.controllers;


import android.content.Context;
import android.util.Log;

import org.osmdroid.util.BoundingBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiTypeDao;
import io.objectbox.BoxStore;

/**
 * Database controller which initializes all Model Dao objects
 * Use this statically to use dao models or boxstore
 *
 * @author Tobias Rüegsegger, Simon Kaspar
 * @license MIT
 */
public final class DatabaseController {
    private static class Holder {
        private static final DatabaseController INSTANCE = new DatabaseController();
    }

    private static BoxStore BOX_STORE;

    private static Context CONTEXT;

    public static DatabaseController createInstance(Context context){

        BOX_STORE = MyObjectBox.builder().androidContext(context).build();
        CONTEXT = context;

        return Holder.INSTANCE;
    }

    public static DatabaseController getInstance(){
        return CONTEXT != null ? Holder.INSTANCE : null;
    }

    public static Context getMainContext() {
        return CONTEXT;
    }

    public static BoxStore getBoxStore() {
        return BOX_STORE;
    }


    //in bytes
    private final long MAXCACHESIZE;
    private final Context mainContext;
    private final BoxStore boxStore;
    private List<DatabaseListener> listeners = new ArrayList<>();
    private boolean syncingPoiTypes;
    private boolean syncingPois;
    private Date lastSync;

    private final LinkedList<DownloadedImage> downloadedImages;
    private long cacheSize;

    private DatabaseController(){
        boxStore = BOX_STORE;
        mainContext = CONTEXT;

        MAXCACHESIZE = 20_000_000;

        listeners = new ArrayList<>();
        downloadedImages = new LinkedList<>();
    }

    /**
     * Deletes all files in the frontend database
     */
    public void flushDatabase() {
        boxStore.deleteAllFiles();
    }

    /**
     * Deletes all pois from the frontend database
     */
    public void deleteAllPois() {
        PoiDao.getInstance().deleteAll();
    }


    /**
     * syncs models based on syncType
     *
     * @param event
     */
    public void sync(DatabaseEvent event) {

        lastSync = new Date();
        switch (event.getType()) {
            case POITYPE:
                if (!syncingPoiTypes) {
                    syncingPoiTypes = true;
                    PoiTypeDao.getInstance().syncTypes();
                }
                break;
            case POIAREA:
                if (!syncingPois) {
                    syncingPois = true;
                    BoundingBox box = (BoundingBox) event.getObj();
                    PoiDao.getInstance().syncPois(box);
                }
                break;
            default:
                sendUpdate(event);
        }
    }


    public void syncPoiTypesDone() {
        syncingPoiTypes = false;
        sendUpdate(new DatabaseEvent(DatabaseEvent.SyncType.POITYPE));
    }

    public void syncPoisDone() {
        syncingPois = false;
        sendUpdate(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA));
    }

    /**
     * @return the time when the last sync !STARTED!
     */
    public Date lastSync() {
        return lastSync;
    }


    public void addDownloadedImages(List<DownloadedImage> images) {

        for (DownloadedImage image : images) {
            downloadedImages.add(image);
            cacheSize += image.getSize();
        }
        clearCache();
    }

    private void clearCache() {

        //TODO endless loop if userimages are higher than maxchachesize
        while (cacheSize >= MAXCACHESIZE) {
            DownloadedImage image = downloadedImages.getFirst();
            if (image.isPublic()) {
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
//    public static void clearAllDownloadedImages() {
//        List<Long> privatePoiIds = new ArrayList<>();
//        for (Poi poi : poiDao.find(Poi_.isPublic, false)) {
//            privatePoiIds.add(poi.getPoi_id());
//        }
//        File filesDir = new File(picturesDir);
//        filesDir.mkdir();
//
//        for (File image : filesDir.listFiles()) {
//            String name = image.getName();
//            int dotIndex = name.lastIndexOf('.');
//            if (dotIndex == -1) continue; //not a valid file
//            String extension = name.substring(dotIndex + 1);
//            try {
//                long imageId = Long.parseLong(name.substring(name.indexOf('-') + 1, name.indexOf('.')));
//                if (privatePoiIds.contains(imageId) && extension.equals("jpg")) {
//                    if (!image.delete()) {
//                        Log.e(DatabaseController.class.toString(),
//                                "image " + image.getAbsolutePath() + " could not be deleted");
//                        break;
//                    }
//                }
//            } catch (NumberFormatException e) {
//                continue;
//            }
//        }
//        cacheSize = 0;
//    }
//
//    public static void deletePoiImages(Poi poi) {
//        byte[] images = poi.getImageIds();
//        for (int i = 0; i < poi.getImageCount(); i++) {
//            File image = poi.getImageById(images[i]);
//            if (!image.delete()) {
//                Log.e(DatabaseController.class.toString(),
//                        "image " + image.getAbsolutePath() + " could not be deleted");
//            }
//        }
//    }

    public void sendUpdate(DatabaseEvent event) {
        for (DatabaseListener listener : listeners) {
            listener.update(event);
        }
    }

    public void register(DatabaseListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void unregister(DatabaseListener listener) {
        listeners.remove(listener);
    }

}
