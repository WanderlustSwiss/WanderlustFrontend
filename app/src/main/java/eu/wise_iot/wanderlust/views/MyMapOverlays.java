package eu.wise_iot.wanderlust.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.views.dialog.ViewPoiDialog;
import eu.wise_iot.wanderlust.models.Old.Feedback;
import eu.wise_iot.wanderlust.models.Old.GpxParser;
import eu.wise_iot.wanderlust.services.FeedbackService;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MyMapFragment:
 * @author Fabian Schwander
 * @license MIT
 */
public class MyMapOverlays implements Serializable {
    private static final String TAG = "MyMapOverlays";
    private Activity activity;
    private MapView mapView;

    private MyLocationNewOverlay myLocationNewOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> itemizedOverlayWithFocus;
    private Marker positionMarker;
    private List<Feedback> feedbackList = new ArrayList<>();

    public MyMapOverlays(Activity activity, MapView mapView) {
        this.activity = activity;
        this.mapView = mapView;

        initFeedbackIconsOverlay();
        initScaleBarOverlay();
        initMyLocationNewOverlay();
//        initGpxTourlistOverlay();
    }

    /**
     * initialize scalebar and its position
     */
    private void initScaleBarOverlay() {
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        //set position of scale bar
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels/3*1, dm.heightPixels/10*9);
        mapView.getOverlays().add(scaleBarOverlay);
    }

    private void initMyLocationNewOverlay() {
        // create location provider and add network provider to the already included gps provider
        GpsMyLocationProvider locationProvider = new GpsMyLocationProvider(activity);
        locationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        Log.i(TAG, "Location sources: " + locationProvider.getLocationSources());

        myLocationNewOverlay = new MyLocationNewOverlay(locationProvider, mapView);
        mapView.getOverlays().add(myLocationNewOverlay);
    }

    private void initFeedbackIconsOverlay(){


        //TODO move
        initItemizedOverlayWithFocus();
        populateFeedbackOverlay();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Defaults.URL_SERVER)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedbackService service = retrofit.create(FeedbackService.class);
        Call<List<Feedback>> call = service.loadFeedbackList();
        call.enqueue(new Callback<List<Feedback>>() {
            @Override
            public void onResponse(Call<List<Feedback>> call, Response<List<Feedback>> response) {
                feedbackList = response.body();
                initItemizedOverlayWithFocus();

                //TODO outcommented to make it run
                //populateFeedbackOverlay();
            }

            @Override
            public void onFailure(Call<List<Feedback>> call, Throwable t) {
                Toast.makeText(activity, R.string.msg_e_feedback_loading_error, Toast.LENGTH_LONG).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    private void initItemizedOverlayWithFocus() {
        // add items with on click listener plus define actions for clicks
        List<OverlayItem> itemizedIconsList = new ArrayList<>();
        itemizedOverlayWithFocus = new ItemizedOverlayWithFocus<>(activity, itemizedIconsList,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem overlayItem) {
                        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
                        // make shure that no other dialog is running
                        Fragment prevFragment = activity.getFragmentManager().findFragmentByTag(Constants.DISPLAY_FEEDBACK_DIALOG);
                        if (prevFragment != null) fragmentTransaction.remove(prevFragment);
                        fragmentTransaction.addToBackStack(null);

                        ViewPoiDialog dialogFragment = ViewPoiDialog.newInstance(overlayItem);
                        dialogFragment.show(fragmentTransaction, Constants.DISPLAY_FEEDBACK_DIALOG);
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem overlayItem) {
                        // TODO: maybe add action when item is pressed long?
                        Toast.makeText(activity, String.format("Feedback-Bild: %s ", overlayItem.getTitle()), Toast.LENGTH_LONG).show();
                        return false;
                    }
                });
    }

    private void initGpxTourlistOverlay() { // FIXME: overlay not working yet -> enable drawing routes!
        GpxParser gpxParser = new GpxParser(activity);
        List<TrackPoint> gpxList = gpxParser.getTrackPointList(R.raw.gpx1);
        ArrayList<GeoPoint> geoPointList = new ArrayList<>();

        for (TrackPoint model : gpxList) {
            GeoPoint newPoint = new GeoPoint(model.getLatitude(), model.getLongitude());
            geoPointList.add(newPoint);
        }

        RoadManager roadManager = new OSRMRoadManager(activity);
        Road road = roadManager.getRoad(geoPointList);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();
    }


    public void addFeedbackIconToOverlay(Feedback feedback) {
        long feedbackId = feedback.getId();
        int feedbackType = feedback.getFeedbackType();
        String imageName = feedback.getImageName();
        String description = feedback.getDescription();
        GeoPoint geoPoint = new GeoPoint(feedback.getLat(), feedback.getLon());
        Drawable drawable = null;


        // check if img exists
        int checkImageIdentifier = activity.getResources().getIdentifier(feedback.getImageNameWithoutSuffix(), "drawable", activity.getPackageName());
        boolean hasImage = false;
        // if identifier == 0, img does NOT exist
        if (checkImageIdentifier != 0) {
            hasImage = true;
        } else  hasImage = false;

        switch (feedbackType) {
            case Constants.TYPE_POSITIVE:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_positive);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_positive_nophoto);
                break;
            case Constants.TYPE_NEGATIVE:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_negative);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_negative_nophoto);
                break;
            case Constants.TYPE_ALERT:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_alert);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_alert_nophoto);
                break;
        }
        if (drawable != null) {
            String id = Long.toString(feedbackId);
            OverlayItem overlayItem = new OverlayItem(id, imageName, description, geoPoint);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            drawable = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));

            overlayItem.setMarker(drawable);
            itemizedOverlayWithFocus.addItem(overlayItem);
            mapView.invalidate();
        }
    }

    public void addPoiToOverlay(Poi poi){
        Drawable drawable;

        //TODO initialize poi with empty list
        boolean hasImage = poi.getImagePath() != null;
        switch ((int)poi.getType()) {
            case 0:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_positive);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_positive_nophoto);
                break;
            case 1:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_negative);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_negative_nophoto);
                break;
            case 2:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_alert);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_alert_nophoto);
                break;
            default:
                drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_positive);
        }

            //TODO ask fabian what this is
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            drawable = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));


            //TODO not sure if what to put to image title and if other values are correct
            OverlayItem overlayItem = new OverlayItem(Long.toString(poi.getPoi_id()), poi.getTitle(),
                    poi.getDescription(), new GeoPoint(poi.getLatitude(), poi.getLongitude()));

            overlayItem.setMarker(drawable);
            itemizedOverlayWithFocus.addItem(overlayItem);
            mapView.invalidate();

    }

    public void addPositionMarker(GeoPoint geoPoint) {
        if (geoPoint != null) {
            // TODO: Move position of icon so that the pointy end marks the exact position of the user
            Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_location_on_highlighted_40dp);

            positionMarker = new Marker(mapView);
            positionMarker.setIcon(drawable);
            positionMarker.setPosition(geoPoint);
            positionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            positionMarker.setTitle(activity.getString(R.string.msg_last_known_position_marker));

            mapView.getOverlays().add(positionMarker);
            mapView.invalidate();
        }
    }

    private void populateFeedbackOverlay() {

        //Feedback is a Poi now!
        /*
        for (Feedback feedback : feedbackList) {
            addFeedbackIconToOverlay(feedback);
        }
        */
        //TODO mit gashi besprechen ob nicht alle daoModels statisch zu machen.
        //TODO mit einer initialisiermethode zu beginn
        PoiDao poiDao = new PoiDao(MainActivity.boxStore);
        List<Poi> pois = poiDao.find();
        for(Poi poi : pois){
            addPoiToOverlay(poi);
        }


        mapView.getOverlays().add(itemizedOverlayWithFocus);
        mapView.invalidate();
    }

    public void removePositionMarker() {
        mapView.getOverlays().remove(positionMarker);
    }

    void showOverlay(boolean showOverlay) {
        if (showOverlay) displayOverlay(itemizedOverlayWithFocus);
        else hideOverlay(itemizedOverlayWithFocus);
    }

    private void displayOverlay(Overlay overlay) {
        mapView.getOverlays().add(overlay);
        mapView.invalidate();
    }

    private void hideOverlay(Overlay overlay) {
        mapView.getOverlays().remove(overlay);
        mapView.invalidate();
    }

    public MyLocationNewOverlay getMyLocationNewOverlay() {
        return myLocationNewOverlay;
    }
}
