package eu.wise_iot.wanderlust.controllers;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.views.MainActivity;

public class CreateTourBackgroundTask extends Service {
    private static final String TAG = "CreateTourContoller";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 3000;
    private static final float LOCATION_DISTANCE = 10f;
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
         * Time difference threshold set for one minute.
         */
        static final int TIME_DIFFERENCE_THRESHOLD = 15 * 1000;

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
            } else { // if there is no accurant signal the last 20 seconds, just take the location
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

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Wanderlust")
                .setContentText("Tracking tour...")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);
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

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void sendLocationBroadcastTrackingFinished() {
        Intent intent = new Intent(Constants.CREATE_TOUR_INTENT);
        Bundle args = new Bundle();
        args.putSerializable(Constants.CREATE_TOUR_TRACK, track);
        intent.putExtra(Constants.CREATE_TOUR_BUNDLE, args);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

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
    String getProviderName() {
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