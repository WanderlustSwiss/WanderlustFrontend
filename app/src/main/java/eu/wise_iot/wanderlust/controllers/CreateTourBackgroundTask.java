package eu.wise_iot.wanderlust.controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.views.MainActivity;

/*
 * A background service which is collecting geoPoints for Creating a tour.
 * @author Joshua Meier
 * @license MIT
 */
public class CreateTourBackgroundTask extends Service {
    private static final String TAG = "CreateTourContoller";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 3000;
    private static final float LOCATION_DISTANCE = 0f;
    private ArrayList<GeoPoint> track = new ArrayList<>();
    private boolean wholeRouteRequired = false;

    private class LocationListener implements android.location.LocationListener {

        Location mLastLocation;

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            doWorkWithNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }

        /**
         * Make use of location after deciding if it is better than previous one.
         *
         * @param location Newly acquired location.
         */
        void doWorkWithNewLocation(Location location) {
            if (isBetterLocation(mLastLocation, location)) {
                GeoPoint addedGeopoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                track.add(addedGeopoint);
                sendLocationBroadcastUpdateMapOverlay(addedGeopoint);
            }
            mLastLocation = location;
        }

        /**
         * Time difference threshold set for 12 seconds (after 12 seconds bad signal, just take the point).
         */
        static final int TIME_DIFFERENCE_THRESHOLD = 12 * 1000;

        /**
         * Decide if new location is better than older by following some basic criteria.
         * This algorithm can be as simple or complicated as your needs dictate it.
         * Try experimenting and get your best location strategy algorithm.
         *
         * @param oldLocation Old location used for comparison.
         * @param newLocation Newly acquired location compared to old one.
         * @return If new location is more accurate and suits your criteria more than the old one.
         */
        boolean isBetterLocation(Location oldLocation, Location newLocation) {
            if (oldLocation == null) {
                return true;
            }
            boolean isMoreAccurate = newLocation.getAccuracy() <= oldLocation.getAccuracy();
            if (isMoreAccurate) {
                return true;
            } else { // if the signal hasn't approved the last 15 seconds, just take the location
                long timeDifference = newLocation.getTime() - oldLocation.getTime();
                if (timeDifference > -TIME_DIFFERENCE_THRESHOLD) {
                    return true;
                }
            }

            return false;
        }
    }

    LocationListener mLocationListeners = new LocationListener();

    public CreateTourBackgroundTask() {
        super();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        initializeLocationManager();
        startForeground();
        LocalBroadcastManager.getInstance(this).registerReceiver(wholeTourRequiredReceiver, new IntentFilter(Constants.CREATE_TOUR_WHOLE_ROUTE_REQUIRED));


        try {
            mLocationManager.requestLocationUpdates(
                    getProviderName(), LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }

    }

    /**
     * Starts the Background Task in Foreground (visible for user) so that the user can see that a tour is recording and so that the system doesn't kill the long living background task.
     */
    private void startForeground() {

        PendingIntent contentIntent;
        contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setColor(this.getResources().getColor(R.color.ap_white))
                        .setContentTitle(getString(R.string.app_name))
                        .setOngoing(true)
                        .setAutoCancel(false)
                        .setContentText(getString(R.string.create_tour_tracking_tour));


        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(1818, mBuilder.build());

        Notification notification = mBuilder.build();
        startForeground(1818, notification);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(mLocationListeners);
            } catch (Exception ex) {
                Log.i(TAG, "fail to remove location listner, ignore", ex);
            }
        }
        sendLocationBroadcastTrackingFinished();
    }

    /**
     * Initializes the Location Manager.
     */
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    /**
     * Sends a Local Broadcast that the tracking is finished with the whole tour as parameter.
     */
    private void sendLocationBroadcastTrackingFinished() {
        Intent intent = new Intent(Constants.CREATE_TOUR_INTENT);
        Bundle args = new Bundle();
        args.putSerializable(Constants.CREATE_TOUR_TRACK, track);
        intent.putExtra(Constants.CREATE_TOUR_BUNDLE, args);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * If wholeRouteRequired is true, it send the whole Route as a Local Broadcast, else only the newest geoPoint
     *
     * @param geoPoint The the newly recorded geopoint which needs to be added in the frontend tracking view.
     */
    private void sendLocationBroadcastUpdateMapOverlay(GeoPoint geoPoint) {
        Bundle args = new Bundle();

        if (!wholeRouteRequired) {
            Log.e(TAG, "Point is sent");
            Intent intent = new Intent(Constants.CREATE_TOUR_UPDATE_MYOVERLAY);
            args.putSerializable(Constants.CREATE_TOUR_ADDING_GEOPOINT, geoPoint);
            intent.putExtra(Constants.CREATE_TOUR_BUNDLE, args);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        } else {
            Log.e(TAG, "Whole route is sent");
            Intent intent = new Intent(Constants.CREATE_TOUR_UPDATE_MYOVERLAY);
            args.putSerializable(Constants.CREATE_TOUR_ADDING_GEOPOINTS, track);
            intent.putExtra(Constants.CREATE_TOUR_BUNDLE, args);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            wholeRouteRequired = false;
        }

    }

    /**
     * Is listening for the duty to send the whole tracking tour.
     */
    private BroadcastReceiver wholeTourRequiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            wholeRouteRequired = true;
            Log.e(TAG, "The whole route is required");
            sendLocationBroadcastUpdateMapOverlay(null);
        }
    };

    /**
     * Get provider name.
     *
     * @return Name of best suiting provider.
     */
    private String getProviderName() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setSpeedRequired(true);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);

        return locationManager.getBestProvider(criteria, true);
    }

}