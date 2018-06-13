package eu.wise_iot.wanderlust.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.SavedTour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseObject.CommunityTourDao;
import eu.wise_iot.wanderlust.views.WanderlustMapView;

/**
 * Handles logic related to Images
 *
 * @author Baris Demirci
 * @author Alexander Weinbeck
 * @license MIT
 */
public class MapCacheHandler {
    private static final String TAG = "MapCacheHandler";

    private final Context context;
    private final Tour tour;
    //private final MapView mapView;
    private int progressPercentage = 10;

    public MapCacheHandler(final Context context, final Tour tour){
        this.context = context;
        this.tour = tour;
        //mapView = new MapView(context);
    }

    public void downloadMap(SavedTour tour, FragmentHandler handler){
        double minLat = 9999;
        double maxLat = -9999;
        double minLong = 9999;
        double maxLong = -9999;

        for (final GeoPoint point : tour.getGeoPoints()) {
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
            if (BuildConfig.DEBUG) Log.d(TAG, "Tile already downloaded!");
            handler.onResponse(new ControllerEvent(EventType.DOWNLOAD_ALREADY_DONE));
        }

        //check if limit reached
        final long cacheLimit = cacheManager.cacheCapacity() - 100000000;
        if(cacheManager.currentCacheUsage() < cacheLimit) {

            final int max = cacheManager.possibleTilesInArea(boundingBox, 10, 15);
            final int notificationID = 900;

            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            final Notification.Builder notificationBuilder = new Notification.Builder(context.getApplicationContext());
            notificationBuilder.setOngoing(true)
                    .setSmallIcon(R.drawable.loader)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(tour.getTitle() + " wird heruntergeladen...")
                    .setProgress(max, 0, false)
                    .setAutoCancel(true);

            final Notification notification = notificationBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
            notificationManager.notify(notificationID, notification);
            if (BuildConfig.DEBUG) Log.d(TAG, "starting download");

            cacheManager.downloadAreaAsyncNoUI(context, boundingBox, 10, 15, new CacheManager.CacheManagerCallback() {
                @Override
                public void downloadStarted() {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Download started");
                    handler.onResponse(new ControllerEvent(EventType.PROGRESS_NOTIFICATION, "STARTED"));
                }

                @Override
                public void updateProgress(final int progress, final int currentZoomLevel, final int zoomMin, final int zoomMax) {
                    if (progress >= ((max / 100) * progressPercentage)) {
                        if (BuildConfig.DEBUG)
                            Log.d(TAG, "refreshing download progress" + progress);
                        notificationBuilder.setProgress(max, progress, false);
                        final Notification notification = notificationBuilder.build();
                        notification.flags = Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS;
                        notificationManager.notify(notificationID, notification);
                        progressPercentage += 5;
                        handler.onResponse(new ControllerEvent(EventType.PROGRESS_UPDATE, progressPercentage));
                    }
                }

                @Override
                public void setPossibleTilesInArea(final int total) {
                }

                @Override
                public void onTaskComplete() {
                    notificationManager.cancel(notificationID);
                    if (BuildConfig.DEBUG) Log.d(TAG, "Download finished");
                    CommunityTourDao.getInstance().create(tour);
                    if (BuildConfig.DEBUG) Log.d(TAG, "Download is saved in internal database");
                    handler.onResponse(new ControllerEvent(EventType.DOWNLOAD_OK));
                }

                @Override
                public void onTaskFailed(final int errors) {
                    notificationManager.cancel(notificationID);
                    if (BuildConfig.DEBUG) Log.d(TAG, "Download finished");
                    handler.onResponse(new ControllerEvent(EventType.DOWNLOAD_FAILED));
                }
            });
        } else {
            handler.onResponse(new ControllerEvent(EventType.DOWNLOAD_NO_SPACE));
        }
    }

    public void deleteMap(){
        double minLat = 9999;
        double maxLat = -9999;
        double minLong = 9999;
        double maxLong = -9999;

        for (final GeoPoint point : tour.getGeoPoints()) {
            if (point.getLatitude() < minLat)
                minLat = point.getLatitude();
            if (point.getLatitude() > maxLat)
                maxLat = point.getLatitude();
            if (point.getLongitude() < minLong)
                minLong = point.getLongitude();
            if (point.getLongitude() > maxLong)
                maxLong = point.getLongitude();
        }

        final MapView mapView = new WanderlustMapView(context);
        final BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
        mapView.zoomToBoundingBox(boundingBox, false);

        final CacheManager cacheManager = new CacheManager(mapView);

        cacheManager.cleanAreaAsync(context, boundingBox, 10, 20).addCallback(new CacheManager.CacheManagerCallback() {
            @Override
            public void onTaskComplete() {
                if (BuildConfig.DEBUG) Log.d(TAG, "Tour deleted, Cache Usage/Capacity:" + String.valueOf(
                        cacheManager.currentCacheUsage()) + '/' + String.valueOf(cacheManager.cacheCapacity()));
            }

            @Override
            public void updateProgress(final int progress, final int currentZoomLevel, final int zoomMin, final int zoomMax) {

            }

            @Override
            public void downloadStarted() {

            }

            @Override
            public void setPossibleTilesInArea(final int total) {

            }

            @Override
            public void onTaskFailed(final int errors) {
                if (BuildConfig.DEBUG) Log.d(TAG, "Tour deleted, Cache Usage/Capacity:" + String.valueOf(
                        cacheManager.currentCacheUsage()) + '/' + String.valueOf(cacheManager.cacheCapacity()));
            }
        });
    }
}
