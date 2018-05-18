package eu.wise_iot.wanderlust.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.views.WanderlustMapView;

/**
 * Created by Baris Demirci on 26.04.2018.
 */

public class MapCacheHandler {
    private static final String TAG = "MapCacheHandler";

    private final Context context;
    private final Tour tour;
    private final MapView mapView;
    private int progressPercentage = 10;

    public MapCacheHandler(Context context, Tour tour){
        this.context = context;
        this.tour = tour;
        mapView = new MapView(context);
    }

    public boolean downloadMap(){
        ArrayList<GeoPoint> geoPoints = (ArrayList<GeoPoint>) tour.getGeoPoints();

        double minLat = 9999;
        double maxLat = -9999;
        double minLong = 9999;
        double maxLong = -9999;

        for (GeoPoint point : geoPoints) {
            if (point.getLatitude() < minLat)
                minLat = point.getLatitude();
            if (point.getLatitude() > maxLat)
                maxLat = point.getLatitude();
            if (point.getLongitude() < minLong)
                minLong = point.getLongitude();
            if (point.getLongitude() > maxLong)
                maxLong = point.getLongitude();
        }

        MapView mapView = new WanderlustMapView(context);
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
        mapView.zoomToBoundingBox(boundingBox, false);

        CacheManager cacheManager = new CacheManager(mapView);

        //check if already in cache
        if(cacheManager.checkTile(new MapTile(10, (int) mapView.getX(), (int) mapView.getY()))){
            return true;
        }

        //check if limit reached
        long cacheLimit = cacheManager.cacheCapacity() - 100000000;
        if(cacheManager.currentCacheUsage() < cacheLimit){

            int max = cacheManager.possibleTilesInArea(boundingBox, 10, 20);
            int notificationID = 900;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder notificationBuilder = new Notification.Builder(context.getApplicationContext());
            notificationBuilder.setOngoing(true)
                               .setSmallIcon(R.mipmap.ic_launcher)
                               .setContentTitle(tour.getTitle() + " wird heruntergeladen..")
                               .setProgress(max, 0, false)
                               .setAutoCancel(true);

            Notification notification = notificationBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
            notificationManager.notify(notificationID, notification);

            cacheManager.downloadAreaAsync(context, boundingBox, 10, 20, new CacheManager.CacheManagerCallback() {
                @Override
                public void onTaskComplete() {
                    notificationManager.cancel(notificationID);
                    Log.d(TAG, "Download finished");
                }

                @Override
                public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {

                    int step = (max / 100);
                    if(progress == step * progressPercentage){
                        notificationBuilder.setProgress(max, progress, false);
                        Notification notification = notificationBuilder.build();
                        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
                        notificationManager.notify(notificationID, notification);
                        progressPercentage += 10;
                    }
                }

                @Override
                public void downloadStarted() {
                    Log.d(TAG, "Download started");
                }

                @Override
                public void setPossibleTilesInArea(int total) {
                }

                @Override
                public void onTaskFailed(int errors) {
                    notificationManager.cancel(notificationID);
                    Log.d(TAG, "Download finished");
                }
            });

        }else{
            return false;
        }

        return true;

    }

    public void deleteMap(){
        ArrayList<GeoPoint> geoPoints = (ArrayList<GeoPoint>) tour.getGeoPoints();

        double minLat = 9999;
        double maxLat = -9999;
        double minLong = 9999;
        double maxLong = -9999;

        for (GeoPoint point : geoPoints) {
            if (point.getLatitude() < minLat)
                minLat = point.getLatitude();
            if (point.getLatitude() > maxLat)
                maxLat = point.getLatitude();
            if (point.getLongitude() < minLong)
                minLong = point.getLongitude();
            if (point.getLongitude() > maxLong)
                maxLong = point.getLongitude();
        }

        MapView mapView = new WanderlustMapView(context);
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
        mapView.zoomToBoundingBox(boundingBox, false);

        CacheManager cacheManager = new CacheManager(mapView);

        CacheManager.CacheManagerTask cacheManagerTask = cacheManager.cleanAreaAsync(context, boundingBox, 10, 20);
        cacheManagerTask.addCallback(new CacheManager.CacheManagerCallback() {
            @Override
            public void onTaskComplete() {
                Log.d(TAG, "Tour deleted, Cache Usage/Capacity:" + String.valueOf(
                        cacheManager.currentCacheUsage()) + "/" + String.valueOf(cacheManager.cacheCapacity()));
            }

            @Override
            public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {

            }

            @Override
            public void downloadStarted() {

            }

            @Override
            public void setPossibleTilesInArea(int total) {

            }

            @Override
            public void onTaskFailed(int errors) {
                Log.d(TAG, "Tour deleted, Cache Usage/Capacity:" + String.valueOf(
                        cacheManager.currentCacheUsage()) + "/" + String.valueOf(cacheManager.cacheCapacity()));
            }
        });
    }
}
