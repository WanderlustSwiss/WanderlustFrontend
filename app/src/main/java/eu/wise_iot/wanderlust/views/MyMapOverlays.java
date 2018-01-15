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
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseListener;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.Old.GpxParser;
import eu.wise_iot.wanderlust.views.dialog.ViewPoiDialog;
import io.ticofab.androidgpxparser.parser.domain.TrackPoint;

/**
 * MyMapFragment:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class MyMapOverlays implements Serializable, DatabaseListener {
    private static final String TAG = "MyMapOverlays";
    private Activity activity;
    private MapView mapView;
    private Polyline currentTour;

    private MyLocationNewOverlay myLocationNewOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> poiOverlay;
    private Marker positionMarker;

    public MyMapOverlays(Activity activity, MapView mapView) {
        this.activity = activity;
        this.mapView = mapView;
        this.currentTour = null;
        initPoiOverlay();
        //populatePoiOverlay();
        mapView.getOverlays().add(poiOverlay);
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
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 3 * 1, dm.heightPixels / 10 * 9);
        mapView.getOverlays().add(scaleBarOverlay);
    }

    public void setTour(Polyline polyline){
        if(this.currentTour == null){
            this.currentTour = polyline;
            this.currentTour.setWidth(10);
            this.currentTour.setColor(R.color.highlight_main);
            mapView.getOverlays().add(this.currentTour);
        }else{
            this.currentTour = polyline;
        }
        mapView.invalidate();
    }

    private void initMyLocationNewOverlay() {
        // create location provider and add network provider to the already included gps provider
        GpsMyLocationProvider locationProvider = new GpsMyLocationProvider(activity);
        locationProvider.addLocationSource(LocationManager.NETWORK_PROVIDER);
        Log.i(TAG, "Location sources: " + locationProvider.getLocationSources());

        myLocationNewOverlay = new MyLocationNewOverlay(locationProvider, mapView);
        mapView.getOverlays().add(myLocationNewOverlay);
    }

    private void initPoiOverlay() {
        // add items with on click listener plus define actions for clicks
        List<OverlayItem> poiList = new ArrayList<>();
        poiOverlay = new ItemizedOverlayWithFocus<>(activity, poiList,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem poiOverlayItem) {
                        long poiId = Long.valueOf(poiOverlayItem.getUid());
                        PoiController controller = new PoiController();
                        controller.getPoiById(poiId, event -> {
                            switch (event.getType()) {
                                case OK:
                                    Poi poi = (Poi) event.getModel();


                                    FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
                                    // make sure that no other dialog is running
                                    Fragment prevFragment = activity.getFragmentManager().findFragmentByTag(Constants.DISPLAY_FEEDBACK_DIALOG);
                                    if (prevFragment != null)
                                        fragmentTransaction.remove(prevFragment);
                                    fragmentTransaction.addToBackStack(null);

                                    ViewPoiDialog dialogFragment = ViewPoiDialog.newInstance(poi);
                                    dialogFragment.show(fragmentTransaction, Constants.DISPLAY_FEEDBACK_DIALOG);
                                    break;
                                default:
                                    //TODO some kind of toast?
                            }
                        });
                        return true;
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem overlayItem) {
                        // TODO: maybe add action when item is pressed long?
                        return false;
                    }
                });
    }

//    private void initGpxTourlistOverlay() { // FIXME: overlay not working yet -> enable drawing routes!
//        GpxParser gpxParser = new GpxParser(activity);
//        List<TrackPoint> gpxList = gpxParser.getTrackPointList(R.raw.gpx1);
//        ArrayList<GeoPoint> geoPointList = new ArrayList<>();
//
//        for (TrackPoint model : gpxList) {
//            GeoPoint newPoint = new GeoPoint(model.getLatitude(), model.getLongitude());
//            geoPointList.add(newPoint);
//        }
//
//        RoadManager roadManager = new OSRMRoadManager(activity);
//        Road road = roadManager.getRoad(geoPointList);
//        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
//        mapView.getOverlays().add(roadOverlay);
//        mapView.invalidate();
//    }

    /**
     * Adds a poi on the mapview with the icon based on
     * the poi type
     */
    public void addPoiToOverlay(Poi poi) {
        Drawable drawable;
        boolean hasImage = poi.getImageCount() > 0;
        switch ((int) poi.getType()) {
            case Constants.TYPE_VIEW:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_sight);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_sight_no_image);
                break;
            case Constants.TYPE_RESTAURANT:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_restaurant);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_restaurant_no_image);
                break;
            case Constants.TYPE_REST_AREA:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_resting);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_resting_no_image);
                break;
            case Constants.TYPE_FLORA_FAUNA:
                if (hasImage)
                    drawable = activity.getResources().getDrawable(R.drawable.icon_flora_fauna);
                else
                    drawable = activity.getResources().getDrawable(R.drawable.icon_flora_fauna_no_image);
                break;
            default:
                drawable = activity.getResources().getDrawable(R.drawable.icon_map_feedback_positive);
        }

        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        drawable = new BitmapDrawable(activity.getResources(), Bitmap.createScaledBitmap(bitmap, 80, 80, true));

        OverlayItem overlayItem = new OverlayItem(Long.toString(poi.getPoi_id()), poi.getTitle(),
                poi.getDescription(), new GeoPoint(poi.getLatitude(), poi.getLongitude()));

        overlayItem.setMarker(drawable);
        poiOverlay.addItem(overlayItem);
    }

    public void addPositionMarker(GeoPoint geoPoint) {
        if (geoPoint != null) {
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


    /**
     * Take all pois from the database and add
     * them to the map overlay
     */
    public void populatePoiOverlay() {

        poiOverlay.removeAllItems();
        List<Poi> pois = DatabaseController.poiDao.find();
        for (Poi poi : pois) {
            addPoiToOverlay(poi);
        }
        mapView.invalidate();
    }

    public void removePositionMarker() {
        mapView.getOverlays().remove(positionMarker);
    }

    void showPoiLayer(boolean setVisible) {
        if (setVisible) {
            if (!mapView.getOverlays().contains(poiOverlay)) {
                mapView.getOverlays().add(poiOverlay);
            }
        } else {
            mapView.getOverlays().remove(poiOverlay);
        }
        mapView.invalidate();
    }

    @Override
    public void update(DatabaseEvent event) {

        if (event.getType() == DatabaseEvent.SyncType.POIAREA) {
            populatePoiOverlay();
        } else if (event.getType() == DatabaseEvent.SyncType.SINGLEPOI) {
            //More efficient, Stamm approves
            Poi poi = (Poi) event.getObj();
            addPoiToOverlay(poi);
            mapView.invalidate();
        } else if (event.getType() == DatabaseEvent.SyncType.DELETESINGLEPOI) {
            Poi poi = (Poi) event.getObj();
            for (int i = 0; i < poiOverlay.size(); i++) {
                if (Long.parseLong(poiOverlay.getItem(i).getUid()) == poi.getPoi_id()) {
                    poiOverlay.removeItem(i);
                    break;
                }
            }
            mapView.invalidate();
        } else if (event.getType() == DatabaseEvent.SyncType.EDITSINGLEPOI) {
            Poi poi = (Poi) event.getObj();
            for (int i = 0; i < poiOverlay.size(); i++) {
                if (Long.parseLong(poiOverlay.getItem(i).getUid()) == poi.getPoi_id()) {
                    poiOverlay.removeItem(i);
                    addPoiToOverlay(poi);
                    break;
                }
            }
            mapView.invalidate();
        }
    }

    public MyLocationNewOverlay getMyLocationNewOverlay() {
        return myLocationNewOverlay;
    }
}