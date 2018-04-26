package eu.wise_iot.wanderlust.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseListener;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.GeoObject;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PublicTransportPoint;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.views.dialog.PoiViewDialog;

/**
 * MyMapFragment:
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class MyMapOverlays implements Serializable, DatabaseListener {
    private static final String TAG = "MyMapOverlays";
    private final Activity activity;
    private final WanderlustMapView mapView;
    private Polyline currentTour;
    private final MapController searchMapController;
    private boolean poiFloraFaunaActive, poiViewActive, poiRestaurantActive, poiRestAreaActive;
    private PoiController poiController;

    private MyLocationNewOverlay myLocationNewOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> poiHashtagOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> poiOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> publicTransportOverlay;
    private ItemizedOverlayWithFocus<OverlayItem> sacHutOverlay;
    private WanderlustCompassOverlay compassOverlay;
    private RotationGestureOverlay rotationGestureOverlay;
    private Marker positionMarker;
    private Marker focusedPositionMarker;
    private ArrayList<Polyline> borderLines;
    private List<GeoObject> sacList;
    private final MapFragment mapFragment;
    private ArrayList<GeoPoint> trackingTourPoints;
    private ArrayList<Polyline> trackingTourOverlay;


    public MyMapOverlays(Activity activity, WanderlustMapView mapView, MapController searchMapController, MapFragment fragment) {
        this.mapFragment = fragment;
        this.searchMapController = searchMapController;
        this.activity = activity;
        this.mapView = mapView;
        this.currentTour = null;
        this.poiController = new PoiController();

        initPoiOverlay();
        mapView.getOverlays().add(poiOverlay);
        mapView.getOverlays().add(poiHashtagOverlay);

        initScaleBarOverlay();
        initMyLocationNewOverlay();
        initCompassOverlay();
    }

    private void initCompassOverlay(){
        compassOverlay = new WanderlustCompassOverlay(activity, mapView);
        compassOverlay.enableCompass();
        //compassOverlay.setCompassCenter(350,  180);
        setCompassPos(350, 180);
        mapView.getOverlays().add(compassOverlay);
        mapView.setWanderlustCompassOverlay(compassOverlay);
    }

    public void setCompassPos(float x, float y){
        compassOverlay.setCompassCenter(x, y);
    }

    /**
     * initialize scalebar and its position
     */
    private void initScaleBarOverlay() {
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
        //set position of scale bar
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 3, dm.heightPixels / 10 * 9);
        mapView.getOverlays().add(scaleBarOverlay);
    }

    public void setTour(Polyline polyline) {
        if (this.currentTour == null) {
            this.currentTour = polyline;
            this.currentTour.setWidth(10);
            Context context = DatabaseController.getMainContext();
            this.currentTour.setColor(context.getResources().getColor(R.color.highlight_main_transparent75));
            mapView.getOverlays().add(this.currentTour);
        } else {
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
        List<OverlayItem> poiHashtagList = new ArrayList<>();


        ItemizedIconOverlay.OnItemGestureListener<OverlayItem> listener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
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

                            PoiViewDialog dialogFragment = PoiViewDialog.newInstance(poi);
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
        };

        poiOverlay = new ItemizedOverlayWithFocus<>(activity, poiList, listener);
        poiHashtagOverlay = new ItemizedOverlayWithFocus<>(activity, poiHashtagList, listener);
    }

    private void makeClusteringGreatAgain(){
        RadiusMarkerClusterer poiMarkers = new RadiusMarkerClusterer(activity.getApplicationContext());
        //Drawable poiIcon = getResources().getDrawable(R.drawable.marker_poi_default);

        for (Poi myPoi : poiController.getAllPois()){
            POI poi = poiController.convertPoiToOSMDroidPOI(myPoi);
            Marker poiMarker = new Marker(mapView);
            poiMarker.setTitle(poi.mType);
            poiMarker.setSnippet(poi.mDescription);
            poiMarker.setPosition(poi.mLocation);
            switch (Integer.parseInt(poi.mCategory)) {
                case Constants.TYPE_VIEW:
                    poiMarker.setIcon(activity.getResources().getDrawable(R.drawable.poi_sight));
                    break;
                case Constants.TYPE_RESTAURANT:
                    poiMarker.setIcon(activity.getResources().getDrawable(R.drawable.poi_resaurant));
                    break;
                case Constants.TYPE_REST_AREA:
                    poiMarker.setIcon(activity.getResources().getDrawable(R.drawable.poi_resting));
                    break;
                case Constants.TYPE_FLORA_FAUNA:
                    poiMarker.setIcon(activity.getResources().getDrawable(R.drawable.poi_fauna_flora));
                    break;
                default:
                    poiMarker.setIcon(activity.getResources().getDrawable(R.drawable.poi_error));
            }
            poiMarker.setOnMarkerClickListener((marker, mapView) -> {
                FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
                // make sure that no other dialog is running
                Fragment prevFragment = activity.getFragmentManager().findFragmentByTag(Constants.DISPLAY_FEEDBACK_DIALOG);
                if (prevFragment != null)
                    fragmentTransaction.remove(prevFragment);
                fragmentTransaction.addToBackStack(null);

                PoiViewDialog dialogFragment = PoiViewDialog.newInstance(myPoi);
                dialogFragment.show(fragmentTransaction, Constants.DISPLAY_FEEDBACK_DIALOG);
                return true;
            });
            poiMarkers.add(poiMarker);
        }

        poiMarkers.setIcon(((BitmapDrawable)activity.getResources().getDrawable(R.drawable.marker_cluster)).getBitmap());
        mapView.getOverlays().add(poiMarkers);
        showPoiLayer(true);
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
     * Updates the Hashtag Poi layer
     *
     * @param poiList the list with poi to be added in the layer
     */
    public void updateHashtagPoiLayer(List<Poi> poiList) {

        poiHashtagOverlay.removeAllItems();
        for (Poi poi : poiList) {
            addPoiToHashtagOverlay(poi);
        }
        mapView.invalidate();
        showPoiHashtagLayer(true);

    }

    public void setPoiFloraFaunaActive(boolean value){
        this.poiFloraFaunaActive = value;
    }
    public void setPoiRestAreaActive(boolean value){
        this.poiRestAreaActive = value;
    }
    public void setPoiViewActive(boolean value){
        this.poiViewActive = value;
    }
    public void setPoiRestaurantActive(boolean value){
        this.poiRestaurantActive = value;
    }
    /**
     * Creates an OverlayItem from a poi with item considering the poi type
     */
    private OverlayItem poiToOverlayItem(POI poi) {
        Drawable drawable;
        switch (Integer.parseInt(poi.mCategory)) {
            case Constants.TYPE_VIEW:
                drawable = activity.getResources().getDrawable(R.drawable.poi_sight);
                break;
            case Constants.TYPE_RESTAURANT:
                drawable = activity.getResources().getDrawable(R.drawable.poi_resaurant);
                break;
            case Constants.TYPE_REST_AREA:
                drawable = activity.getResources().getDrawable(R.drawable.poi_resting);
                break;
            case Constants.TYPE_FLORA_FAUNA:
                drawable = activity.getResources().getDrawable(R.drawable.poi_fauna_flora);
                break;
            default:
                drawable = activity.getResources().getDrawable(R.drawable.poi_error);
        }

        OverlayItem overlayItem = new OverlayItem(Long.toString(poi.mId), poi.mType,
                poi.mDescription, poi.mLocation);

        overlayItem.setMarker(drawable);;
        return overlayItem;
    }

    /**
     * Adds a poi on the mapview regular MapOverlay
     * only adds poi if subcategory is selected in frontend
     */
    public void addPoiToOverlay(Poi paramPoi) {
        /*
        POI poi = poiController.convertPoiToOSMDroidPOI(paramPoi);
        if(this.poiViewActive && (Integer.parseInt(poi.mCategory) == Constants.TYPE_VIEW))
            poiOverlay.addItem(poiToOverlayItem(poi));
        else if(this.poiRestaurantActive && (Integer.parseInt(poi.mCategory) == Constants.TYPE_RESTAURANT))
            poiOverlay.addItem(poiToOverlayItem(poi));
        else if (this.poiRestAreaActive && (Integer.parseInt(poi.mCategory) == Constants.TYPE_REST_AREA))
            poiOverlay.addItem(poiToOverlayItem(poi));
        else if (this.poiFloraFaunaActive && (Integer.parseInt(poi.mCategory) == Constants.TYPE_FLORA_FAUNA))
            poiOverlay.addItem(poiToOverlayItem(poi));
        */
    }

    /**
     * Adds a poi to the mapview to the hashtagPoiOverlay
     */
    public void addPoiToHashtagOverlay(Poi paramPoi) {
        POI poi = poiController.convertPoiToOSMDroidPOI(paramPoi);
        OverlayItem overlayItem = poiToOverlayItem(poi);
        poiHashtagOverlay.addItem(overlayItem);
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
        List<Poi> pois = PoiDao.getInstance().find();
        for (Poi poi : pois) {
            addPoiToOverlay(poi);
        }
        mapView.invalidate();
    }

    public void removePositionMarker() {
        mapView.getOverlays().remove(positionMarker);
    }


    /**
     * Triggers the loading of Poi layer
     *
     * @param setVisible display or hide layer
     */
    void showPoiLayer(boolean setVisible) {
        showPoiHashtagLayer(false);
        if (setVisible) {
            if (!mapView.getOverlays().contains(poiOverlay)) {
                mapView.getOverlays().add(poiOverlay);
            }
        } else {
            mapView.getOverlays().remove(poiOverlay);
        }
        mapView.invalidate();
    }

    /**
     * Triggers the refresh of Poi layer
     *
     */
    void refreshPoiLayer() {
        this.populatePoiOverlay();
        mapView.invalidate();
        /*
        mapView.getOverlays().remove(poiOverlay);
        mapView.invalidate();
        mapView.getOverlays().add(poiOverlay);
        mapView.invalidate();
        */
    }

    /**
     * Triggers the loading of public transport layer arround the current location and displays it
     *
     * @param geoPoint   Current location of the user
     * @param setVisible display or hide layer
     */
    void showPublicTransportLayer(boolean setVisible, GeoPoint geoPoint) {
        if (publicTransportOverlay == null) {
            ItemizedIconOverlay.OnItemGestureListener<OverlayItem> listener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    mapFragment.showInformationBottomSheet(true, item.getTitle());
                    return true;
                }

                @Override
                public boolean onItemLongPress(int index, OverlayItem item) {
                    return false;
                }
            };
            publicTransportOverlay = new ItemizedOverlayWithFocus<>(activity, new ArrayList<>(), listener);
        }

        if (setVisible) {
            if (mapView.getZoomLevel() > 14) {
                searchMapController.searchPublicTransportStations(geoPoint, 200, 2000, (ControllerEvent controllerEvent) -> {
                    if (controllerEvent.getType() == EventType.OK) {

                        Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_train_black_24dp);
                        publicTransportOverlay.removeAllItems();

                        for (PublicTransportPoint publicTransportPoint : (List<PublicTransportPoint>) controllerEvent.getModel()) {
                            OverlayItem overlayItem = new OverlayItem(Integer.toString(publicTransportPoint.getId()), publicTransportPoint.getTitle(), publicTransportPoint.getTitle(), publicTransportPoint.getGeoPoint());
                            overlayItem.setMarker(drawable);
                            publicTransportOverlay.addItem(overlayItem);
                        }
                        mapView.getOverlays().add(publicTransportOverlay);
                    }
                    mapView.invalidate();
                });
            }

        } else {
            publicTransportOverlay.removeAllItems();
            mapView.getOverlays().remove(publicTransportOverlay);
            mapView.invalidate();
        }
        mapView.invalidate();

    }

    /**
     * Triggers the loading of Hashtag Search layer
     *
     * @param setVisible display or hide layer
     */
    void showPoiHashtagLayer(boolean setVisible) {
        if (setVisible) {
            if (!mapView.getOverlays().contains(poiHashtagOverlay)) {
                mapView.getOverlays().add(poiHashtagOverlay);
            }
        } else {
            mapView.getOverlays().remove(poiHashtagOverlay);
        }
        mapView.invalidate();
    }


    @Override
    public void update(DatabaseEvent event) {
        /*if (event.getType() == DatabaseEvent.SyncType.POIAREA) {
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
                    //addPoiToOverlay(poi);
                    break;
                }
            }
            mapView.invalidate();
        }*/
        makeClusteringGreatAgain();
        mapView.invalidate();
    }

    /**
     * Adds boarder lines of Search to the map view and deactivates position marker
     *
     * @return geoPoints A List of Geopoint to be displayed
     */
    public MyLocationNewOverlay getMyLocationNewOverlay() {
        return myLocationNewOverlay;
    }


    /**
     * Adds the current position marker
     *
     * @param geoPoint location of position
     */
    public void addFocusedPositionMarker(GeoPoint geoPoint) {
        if (focusedPositionMarker != null) {
            removeFocusedPositionMarker();
        }

        if (geoPoint != null) {
            Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_location_on_highlighted_40dp);

            focusedPositionMarker = new Marker(mapView);
            focusedPositionMarker.setIcon(drawable);
            focusedPositionMarker.setPosition(geoPoint);
            focusedPositionMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            focusedPositionMarker.setTitle(activity.getString(R.string.msg_last_known_position_marker));

            mapView.getOverlays().add(focusedPositionMarker);
            mapView.invalidate();
        }
    }

    /**
     * Deletes the focused position marker
     */
    public void removeFocusedPositionMarker() {
        mapView.getOverlays().remove(focusedPositionMarker);
    }


    /**
     * Adds boarder lines of Search to the map view and deactivates position marker
     *
     * @param geoPoints A List of Geopoint to be displayed
     */
    public void addPolyline(ArrayList<GeoPoint> geoPoints) {
        if (borderLines == null) {
            borderLines = new ArrayList<>();
        }

        Polyline polyline = new Polyline();

        polyline.setPoints(geoPoints);
        polyline.setColor(activity.getResources().getColor(R.color.highlight_main_transparent75));

        borderLines.add(polyline);

        mapView.getOverlays().add(polyline);
        mapView.invalidate();
    }


    /**
     * Deletes the boarder lines of search results (villages etc.)
     */
    public void clearPolylines() {
        if (borderLines != null) {
            mapView.getOverlays().removeAll(borderLines);
            borderLines = null;
        }
    }


    // *********************** SAC LAYER ************************************************** //

    /**
     * Triggers the loading of sac hut layer arround the current location and displays it
     *
     * @param geoPoint1  Current location of the user
     * @param geoPoint2  Current location of the user
     * @param setVisible display or hide layer
     */
    void showSacHutLayer(boolean setVisible, GeoPoint geoPoint1, GeoPoint geoPoint2) {
        if (sacHutOverlay == null) {
            ItemizedIconOverlay.OnItemGestureListener<OverlayItem> listener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(final int index, final OverlayItem poiOverlayItem) {
                    long poiId = Long.valueOf(poiOverlayItem.getUid());

                    GeoObject currentObject = sacList.get((int) poiId);

                    FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
                    // make sure that no other dialog is running
                    Fragment prevFragment = activity.getFragmentManager().findFragmentByTag(Constants.DISPLAY_FEEDBACK_DIALOG);
                    if (prevFragment != null)
                        fragmentTransaction.remove(prevFragment);
                    fragmentTransaction.addToBackStack(null);

                    PoiViewDialog dialogFragment = PoiViewDialog.newInstance(currentObject, Constants.TYPE_SAC);
                    dialogFragment.show(fragmentTransaction, Constants.DISPLAY_FEEDBACK_DIALOG);
                    return true;
                }

                @Override
                public boolean onItemLongPress(final int index, final OverlayItem overlayItem) {
                    // TODO: maybe add action when item is pressed long?
                    return false;
                }
            };

            sacHutOverlay = new ItemizedOverlayWithFocus<>(activity, new ArrayList<>(), listener);
        }

        if (setVisible) {

            searchMapController.searchSac(geoPoint1, geoPoint2, controllerEvent -> {
                if (controllerEvent.getType() == EventType.OK) {
                    sacList = controllerEvent.getModel();

                    Drawable drawable = activity.getResources().getDrawable(R.drawable.ic_home_black_24dp_white);
                    sacHutOverlay.removeAllItems();

                    for (GeoObject sacHut : controllerEvent.getModel()) {
                        GeoPoint sacLocation = new GeoPoint(sacHut.getLatitude(), sacHut.getLongitude());
                        int id = sacHutOverlay.size();
                        OverlayItem overlayItem = new OverlayItem(Integer.toString(id), sacHut.getTitle(), sacHut.getTitle(), sacLocation);
                        overlayItem.setMarker(drawable);
                        sacHutOverlay.addItem(overlayItem);
                    }
                    mapView.getOverlays().add(sacHutOverlay);
                } else {
                    Toast.makeText(activity, R.string.map_nothing_found, Toast.LENGTH_SHORT).show();
                }
                mapView.invalidate();
            });
        } else {
            sacHutOverlay.removeAllItems();
            mapView.getOverlays().remove(sacHutOverlay);
            mapView.invalidate();
        }
        mapView.invalidate();

    }


    /**
     * Adds a GeoPoint to the map view for Tracking tour and deactivates position marker
     *
     * @param geoPoint A point to be added to to tracking overlay
     */
    public void addItemToTrackingOverlay(GeoPoint geoPoint) {
        if (trackingTourPoints == null) {
            trackingTourPoints = new ArrayList<>();
        }
        if (trackingTourOverlay == null) {
            trackingTourOverlay = new ArrayList<>();
        }
        mapView.getOverlays().removeAll(trackingTourOverlay);

        Polyline polyline = new Polyline();
        trackingTourPoints.add(geoPoint);
        polyline.setPoints(this.trackingTourPoints);
        polyline.setColor(activity.getResources().getColor(R.color.highlight_main_transparent75));
        trackingTourOverlay.add(polyline);
        mapView.getOverlays().add(polyline);
        mapView.invalidate();
    }

    /**
     * Deletes the points of the tracking tour overlay
     */
    public void clearTrackingOverlay() {
        if (trackingTourOverlay != null) {
            mapView.getOverlays().removeAll(trackingTourOverlay);
            trackingTourOverlay = null;
            trackingTourPoints = null;
        }
    }

    /**
     * Adds a GeoPoint to the map view for Tracking tour and deactivates position marker
     *
     * @param geoPoints A list of points to be added to to tracking overlay
     */
    public void refreshTrackingOverlay(ArrayList<GeoPoint> geoPoints) {
        clearTrackingOverlay();
        trackingTourPoints = geoPoints;
        trackingTourOverlay = new ArrayList<>();

        Polyline polyline = new Polyline();
        polyline.setPoints(this.trackingTourPoints);
        polyline.setColor(activity.getResources().getColor(R.color.highlight_main_transparent75));
        trackingTourOverlay.add(polyline);
        mapView.getOverlays().add(polyline);
        mapView.invalidate();
    }

}