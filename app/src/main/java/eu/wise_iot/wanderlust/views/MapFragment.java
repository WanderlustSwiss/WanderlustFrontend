package eu.wise_iot.wanderlust.views;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.MatrixCursor;
import android.graphics.Rect;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.TilesOverlay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.controllers.CreateTourBackgroundTask;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.models.DatabaseModel.HashtagResult;
import eu.wise_iot.wanderlust.models.DatabaseModel.MapSearchResult;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.Old.Camera;
import eu.wise_iot.wanderlust.views.animations.StyleBehavior;
import eu.wise_iot.wanderlust.views.dialog.CreateTourDialog;
import eu.wise_iot.wanderlust.views.dialog.PoiEditDialog;

import static android.content.Context.POWER_SERVICE;

/**
 * MapFragment: The Fragment that contains the map view, map functionality and buttons.
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";

    private LinearLayout poiTypeSelection;
    public static String photoPath;
    private static String imageFileName;
    private static DatabaseController databaseController;
    // preferences and default settings
    private SharedPreferences sharedPreferences;
    private boolean locationTogglerHasBeenClicked, myLocationIsEnabled, restAreaActive, viewActive, restaurantActive, floraFaunaActive;
    private int zoomLevel;
    private GeoPoint centerOfMap, lastKnownLocation;
    //private MapView mapView;

    private WanderlustMapView mapView;
    private IMapController mapController;
    private MyMapOverlays mapOverlays;
    private ImageButton ibLocationToggler, cameraButton, layerButton, staliteTypeButton, defaultTypeButton, terrainTypeButton,
                        //bottom sheet
                        ibPoiRestAreaLayer, ibPoiFloraFaunaLayer, ibPoiRestaurantLayer, ibPoiViewLayer,
                        ibPoiLayer, ibPublicTransportLayer, ibSacHutLayer;
    private View bottomSheet;
    private MapController searchMapController;
    private static Polyline polyline;

    // Search suggetstion
    private List<HashtagResult> hashTagSearchSuggestions = new ArrayList<>();
    private SimpleCursorAdapter mAdapter;
    private MatrixCursor c = new MatrixCursor(new String[]{BaseColumns._ID, "hashTag"});

    // Bottom Sheet with information String
    private BottomSheetBehavior informationBottomSheet;
    private TextView informationBottomSheetString;

    // GPS Creating Tour
    private FloatingActionButton createTourButton;
    private TextView creatingTourInformation;
    private Intent createTourIntent;
    private FloatingActionMenu floatingActionMenu;
    private boolean floatingActionMenuExpanded = false;

    /**
     * Static instance constructor.
     *
     * @return Fragment: MapFragment
     */
    public static MapFragment newInstance() {

        Bundle args = new Bundle();
        databaseController = DatabaseController.getInstance();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static MapFragment newInstance(Polyline paramPolyline) {
        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        polyline = paramPolyline;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchMapController = new MapController(this);
        setHasOptionsMenu(true);
        loadPreferences();
        getActivity().setTitle("");

        // For search View
        final String[] from = new String[]{"hashTag"};
        final int[] to = new int[]{android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.li_query_suggestion,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        createTourButton = (FloatingActionButton) view.findViewById(R.id.createTourButton);
        initMap(view);
        initOverlays();
        initMapController();
        initLayerButton(view);
        initPoiTypeButtons(view);
        databaseController.register(mapOverlays);

        if (polyline != null) setTour(polyline);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseController.unregister(mapOverlays);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLocationToggler(view);
        initCameraButton(view);
        initLayerButton(view);
        initMapTypeButton(view);
        initInformationBottomSheet(view);
        initCreatingTourControlls(view);
    }

    private void initCreatingTourControlls(View view) {
        creatingTourInformation = (TextView) view.findViewById(R.id.createTourInformation);
        createTourIntent = new Intent(getActivity(), CreateTourBackgroundTask.class);
        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.menu_floating_button);

        floatingActionMenu.setIconAnimated(false);
        floatingActionMenu.setOnMenuButtonClickListener(view1 -> {
            if (floatingActionMenu.isOpened()) {
                floatingActionMenu.setMenuButtonColorNormalResId(R.color.primary_main);
                floatingActionMenu.close(true);
                floatingActionMenu.getMenuIconView().setImageResource(R.drawable.ic_add_white_24dp);
            }
            else {
                floatingActionMenu.setMenuButtonColorNormalResId(R.color.white);
                floatingActionMenu.open(true);
                floatingActionMenu.getMenuIconView().setImageResource(R.drawable.ic_arrow_downward_black_24dp);
            }

        });

        if (isMyServiceRunning(CreateTourBackgroundTask.class)) {
            createTourButton.setImageResource(R.drawable.ic_stop_red_24dp);
            creatingTourInformation.setVisibility(View.VISIBLE);
        }

        createTourButton.setOnClickListener(view1 -> {

            if (!isMyServiceRunning(CreateTourBackgroundTask.class)) {
                if (startTourTracking()) {
                    createTourButton.setImageResource(R.drawable.ic_track_stop_3);
                    createTourButton.setColorNormalResId(R.color.highlight_main);

                    creatingTourInformation.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getActivity(), R.string.create_tour_need_whitelist, Toast.LENGTH_SHORT).show();
                }
            } else {
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.create_tour_save_tour)
                        .setMessage(R.string.create_tour_stop_recording_request)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, (dialog, positiveButton) -> {
                            stoptTourTracking();
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });
    }

    private void initInformationBottomSheet(View view) {
        View bottomSheet = view.findViewById(R.id.bottom_sheet_public_transport);
        informationBottomSheet = BottomSheetBehavior.from(bottomSheet);
        informationBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);

        informationBottomSheetString = (TextView) view.findViewById(R.id.public_transport_station_name);

        this.mapView.addObserver((arg, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && informationBottomSheet.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                Rect outRect = new Rect();
                bottomSheet.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                    informationBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }
    private void initPoiTypeButtons(View view){
        ibPoiFloraFaunaLayer = (ImageButton) view.findViewById(R.id.poiTypeFloraFauna);
        ibPoiRestaurantLayer = (ImageButton) view.findViewById(R.id.poiTypesRestaurant);
        ibPoiViewLayer = (ImageButton) view.findViewById(R.id.poiTypesView);
        ibPoiRestAreaLayer = (ImageButton) view.findViewById(R.id.poiTypesRestArea);
        poiTypeSelection = (LinearLayout) view.findViewById(R.id.poiTypeSelection);

        LayoutTransition transition = new LayoutTransition();
        transition.setAnimateParentHierarchy(false);
        poiTypeSelection.setLayoutTransition(transition);

        if(ibPoiLayer.isSelected()) poiTypeSelection.setVisibility(View.VISIBLE);
        else poiTypeSelection.setVisibility(View.GONE);

        if(restAreaActive){
            ibPoiRestAreaLayer.setImageResource(R.drawable.ic_local_parking_white_24dp);
            ibPoiRestAreaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
        } else {
            ibPoiRestAreaLayer.setImageResource(R.drawable.ic_local_parking_black_24dp);
            ibPoiRestAreaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
        }
        mapOverlays.setPoiRestAreaActive(restAreaActive);
        ibPoiRestAreaLayer.setSelected(restAreaActive);

        if(floraFaunaActive){
            ibPoiFloraFaunaLayer.setImageResource(R.drawable.ic_local_florist_white_24dp);
            ibPoiFloraFaunaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
        } else {
            ibPoiFloraFaunaLayer.setImageResource(R.drawable.ic_local_florist_black_24dp);
            ibPoiFloraFaunaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
        }
        mapOverlays.setPoiFloraFaunaActive(floraFaunaActive);
        ibPoiFloraFaunaLayer.setSelected(floraFaunaActive);

        if(restaurantActive){
            ibPoiRestaurantLayer.setImageResource(R.drawable.ic_restaurant_white_24dp);
            ibPoiRestaurantLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
        } else {
            ibPoiRestaurantLayer.setImageResource(R.drawable.ic_restaurant_black_24dp);
            ibPoiRestaurantLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
        }
        mapOverlays.setPoiRestAreaActive(restaurantActive);
        ibPoiRestaurantLayer.setSelected(restaurantActive);

        if(viewActive){
            ibPoiViewLayer.setImageResource(R.drawable.ic_terrain_white_24dp);
            ibPoiViewLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
        } else {
            ibPoiViewLayer.setImageResource(R.drawable.ic_terrain_black_24dp);
            ibPoiViewLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
        }
        mapOverlays.setPoiViewActive(viewActive);
        ibPoiViewLayer.setSelected(viewActive);

        ibPoiRestAreaLayer.setOnClickListener(v -> {
            if(ibPoiRestAreaLayer.isSelected()){
                ibPoiRestAreaLayer.setImageResource(R.drawable.ic_local_parking_black_24dp);
                ibPoiRestAreaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
                ibPoiRestAreaLayer.setSelected(false);
            } else {
                ibPoiRestAreaLayer.setImageResource(R.drawable.ic_local_parking_white_24dp);
                ibPoiRestAreaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
                ibPoiRestAreaLayer.setSelected(true);
            }
            mapOverlays.setPoiRestAreaActive(ibPoiRestAreaLayer.isSelected());
            mapOverlays.refreshPoiLayer();
        });
        ibPoiFloraFaunaLayer.setOnClickListener(v -> {
            if(ibPoiFloraFaunaLayer.isSelected()){
                ibPoiFloraFaunaLayer.setImageResource(R.drawable.ic_local_florist_black_24dp);
                ibPoiFloraFaunaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
                ibPoiFloraFaunaLayer.setSelected(false);
            } else {
                ibPoiFloraFaunaLayer.setImageResource(R.drawable.ic_local_florist_white_24dp);
                ibPoiFloraFaunaLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
                ibPoiFloraFaunaLayer.setSelected(true);
            }
            mapOverlays.setPoiFloraFaunaActive(ibPoiFloraFaunaLayer.isSelected());
            mapOverlays.refreshPoiLayer();
        });
        ibPoiRestaurantLayer.setOnClickListener(v -> {
            if(ibPoiRestaurantLayer.isSelected()){
                ibPoiRestaurantLayer.setImageResource(R.drawable.ic_restaurant_black_24dp);
                ibPoiRestaurantLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
                ibPoiRestaurantLayer.setSelected(false);
            } else {
                ibPoiRestaurantLayer.setImageResource(R.drawable.ic_restaurant_white_24dp);
                ibPoiRestaurantLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
                ibPoiRestaurantLayer.setSelected(true);
            }
            mapOverlays.setPoiRestaurantActive(ibPoiRestaurantLayer.isSelected());
            mapOverlays.refreshPoiLayer();
        });
        ibPoiViewLayer.setOnClickListener(v -> {
            if(ibPoiViewLayer.isSelected()){
                ibPoiViewLayer.setImageResource(R.drawable.ic_terrain_black_24dp);
                ibPoiViewLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
                ibPoiViewLayer.setSelected(false);
            } else {
                ibPoiViewLayer.setImageResource(R.drawable.ic_terrain_white_24dp);
                ibPoiViewLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
                ibPoiViewLayer.setSelected(true);
            }
            mapOverlays.setPoiViewActive(ibPoiViewLayer.isSelected());
            mapOverlays.refreshPoiLayer();
        });
    }
    private void initMapTypeButton(View view) {
        staliteTypeButton = (ImageButton) view.findViewById(R.id.map_satelite_type);
        defaultTypeButton = (ImageButton) view.findViewById(R.id.map_default_type);
        terrainTypeButton = (ImageButton) view.findViewById(R.id.map_terrain_type);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        defaultTypeButton.setBackground(getActivity().getDrawable(R.drawable.outline_selected_item_colored));

        staliteTypeButton.setOnClickListener(e -> {
            String[] urlArray = {"http://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/"};
            mapView.setTileSource(new XYTileSource("ARCGisOnline", 0, 18, 256, "", urlArray) {
                @Override
                public String getTileURLString(MapTile aTile) {
                    String mImageFilenameEnding = ".png";
                    return getBaseUrl() + aTile.getZoomLevel() + "/"
                            + aTile.getY() + "/" + aTile.getX()
                            + mImageFilenameEnding;
                }
            });

            defaultTypeButton.setBackground(null);
            terrainTypeButton.setBackground(null);
            staliteTypeButton.setBackground(getActivity().getDrawable(R.drawable.outline_selected_item_colored));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        defaultTypeButton.setOnClickListener(e -> {
            ITileSource tileSource = new XYTileSource("OpenTopoMap", 0, 20, 256, ".png",
                    new String[]{"https://opentopomap.org/"});
            mapView.setTileSource(tileSource);
            TilesOverlay stuff = mapView.getOverlayManager().getTilesOverlay();
            //stuff.setOvershootTileCache(stuff.getOvershootTileCache()*2);
            staliteTypeButton.setBackground(null);
            terrainTypeButton.setBackground(null);
            defaultTypeButton.setBackground(getActivity().getDrawable(R.drawable.outline_selected_item_colored));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        });

        terrainTypeButton.setOnClickListener(e -> {
            ITileSource tileSource = new XYTileSource("Stamen", 0, 20, 256, ".png",
                    new String[]{"http://c.tile.stamen.com/terrain/"});
            mapView.setTileSource(tileSource);
            staliteTypeButton.setBackground(null);
            defaultTypeButton.setBackground(null);
            terrainTypeButton.setBackground(getActivity().getDrawable(R.drawable.outline_selected_item_colored));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        mapView.setBottomSheetClosingComponents(bottomSheet, bottomSheetBehavior);

    }


    @Override
    public void onStart() {
        super.onStart();
        if (myLocationIsEnabled) {
            centerMapOnCurrentPosition();
        }
        // quickfix to make sure that the cameraButton is enabled after beeing disabled in onClick todo: add better way
        cameraButton.setEnabled(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        // disable energy consuming processes
        mapOverlays.getMyLocationNewOverlay().disableMyLocation();
        mapOverlays.getMyLocationNewOverlay().disableFollowLocation();
        if (isMyServiceRunning(CreateTourBackgroundTask.class)) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateTrackingOverlayReceiver);
            Log.e(TAG, "Stop drawing points on map. Energy saver.");
        }
        savePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
        if (isMyServiceRunning(CreateTourBackgroundTask.class)) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateTrackingOverlayReceiver, new IntentFilter(Constants.CREATE_TOUR_UPDATE_MYOVERLAY));
            Intent intent = new Intent(Constants.CREATE_TOUR_WHOLE_ROUTE_REQUIRED);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            Log.e(TAG, "A request for the whole tour is sent and start tracking on map again.");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * Receive the result from a previous call to startActivityForResult(Intent, int)
     *
     * @param requestCode int: The integer request code originally supplied to startActivityForResult(), allowing you to identify who this result came from.
     * @param resultCode  int: The integer result code returned by the child activity through its setResult().
     * @param data        Intent: An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // photo intent finished and image saved
        if (requestCode == Constants.TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //save / publish photo
            dispatchPostFeedbackDialogFragment();
        }
        // photo intent aborted
        if (requestCode == Constants.TAKE_PHOTO && resultCode != Activity.RESULT_OK) {
            //discard photo
            File file = new File(photoPath);
            file.delete();
        }
    }

    public void setTour(Polyline polyline) {
        mapOverlays.setTour(polyline);
        List<GeoPoint> polylineList = polyline.getPoints();
        mapController.setCenter(polylineList.get(0));
        mapController.setZoom(Defaults.ZOOM_ENLARGED);
    }

    /**
     * Loads user preferences of map settings in shared preferences
     */

    private void loadPreferences() {
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        double defaultMapCenterLat = Defaults.GEO_POINT_CENTER_OF_SWITZERLAND.getLatitude();
        double defaultMapCenterLon = Defaults.GEO_POINT_CENTER_OF_SWITZERLAND.getLongitude();
        zoomLevel = sharedPreferences
                .getInt(Constants.LAST_ZOOM_LEVEL, Defaults.ZOOM_STARTUP);
        double lastMapCenterLat = Double.longBitsToDouble(sharedPreferences
                .getLong(Constants.LAST_MAP_CENTER_LAT, Double.doubleToLongBits(defaultMapCenterLat)));
        double lastMapCenterLon = Double.longBitsToDouble(sharedPreferences
                .getLong(Constants.LAST_MAP_CENTER_LON, Double.doubleToLongBits(defaultMapCenterLon)));
        centerOfMap = new GeoPoint(lastMapCenterLat, lastMapCenterLon);

        double lastLocationLat = Double.longBitsToDouble(sharedPreferences.getLong(Constants.LAST_POS_LAT, 0));
        double lastLocationLon = Double.longBitsToDouble(sharedPreferences.getLong(Constants.LAST_POS_LON, 0));
        lastKnownLocation = new GeoPoint(lastLocationLat, lastLocationLon);
        locationTogglerHasBeenClicked = sharedPreferences.getBoolean(Constants.PREFERENCE_BUTTON_LOCATION_CLICKED, false);
        myLocationIsEnabled = sharedPreferences.getBoolean(Constants.PREFERENCE_MY_LOCATION_ENABLED, myLocationIsEnabled);
        restAreaActive = sharedPreferences.getBoolean(Constants.PREFERENCE_POITYPE_RESTAREA_ACTIVE,true);
        viewActive = sharedPreferences.getBoolean(Constants.PREFERENCE_POITYPE_VIEW_ACTIVE,true);
        restaurantActive = sharedPreferences.getBoolean(Constants.PREFERENCE_POITYPE_RESTAURANT_ACTIVE,true);
        floraFaunaActive = sharedPreferences.getBoolean(Constants.PREFERENCE_POITYPE_FLORAFAUNA_ACTIVE,true);
    }

    /**
     * Saves user preferences of map settings in shared preferences
     */
    private void savePreferences() {
        int lastZoomLevel = mapView.getZoomLevel();
        double lastMapCenterLat = mapView.getMapCenter().getLatitude();
        double lastMapCenterLon = mapView.getMapCenter().getLongitude();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.LAST_ZOOM_LEVEL, lastZoomLevel);
        editor.putLong(Constants.LAST_MAP_CENTER_LAT, Double.doubleToRawLongBits(lastMapCenterLat));
        editor.putLong(Constants.LAST_MAP_CENTER_LON, Double.doubleToRawLongBits(lastMapCenterLon));
        editor.putLong(Constants.LAST_POS_LAT, Double.doubleToLongBits(lastKnownLocation.getLatitude()));
        editor.putLong(Constants.LAST_POS_LON, Double.doubleToLongBits(lastKnownLocation.getLongitude()));
        editor.putBoolean(Constants.PREFERENCE_MY_LOCATION_ENABLED, myLocationIsEnabled);
        //poitype selection
        editor.putBoolean(Constants.PREFERENCE_POITYPE_RESTAURANT_ACTIVE, ibPoiRestaurantLayer.isSelected());
        editor.putBoolean(Constants.PREFERENCE_POITYPE_FLORAFAUNA_ACTIVE, ibPoiFloraFaunaLayer.isSelected());
        editor.putBoolean(Constants.PREFERENCE_POITYPE_RESTAREA_ACTIVE, ibPoiRestAreaLayer.isSelected());
        editor.putBoolean(Constants.PREFERENCE_POITYPE_VIEW_ACTIVE, ibPoiViewLayer.isSelected());
        editor.putBoolean(Constants.PREFERENCE_POI_LAYER_ACTIVE, ibPoiLayer.isSelected());
        editor.putBoolean(Constants.PREFERENCE_PUBLICTRANSPORT_LAYER_ACTIVE, ibPublicTransportLayer.isSelected());
        editor.putBoolean(Constants.PREFERENCE_SAC_LAYER_ACTIVE, ibSacHutLayer.isSelected());
        editor.apply();
    }


    /**
     * Initializes map view
     *
     * @param view View: view of current fragment
     */
    private void initMap(View view) {
        mapView = (WanderlustMapView) view.findViewById(R.id.mapView);
        ITileSource tileSource = new XYTileSource("OpenTopoMap", 0, 20, 256, ".png",
                new String[]{"https://opentopomap.org/"});
        mapView.setTileSource(tileSource);
        mapView.setTilesScaledToDpi(true);
        mapView.setMultiTouchControls(true);
    }

    /**
     * Initializes map controller
     */
    private void initMapController() {
        mapController = mapView.getController();
        mapView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            //Gefährlich aber legit
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                WanderlustMapView map = (WanderlustMapView) v;

                if (round(map.getMapCenter().getLatitude()) == round(centerOfMap.getLatitude())
                        && round(map.getMapCenter().getLongitude()) == round(centerOfMap.getLongitude())) {
                    databaseController.sync(new DatabaseEvent<>(DatabaseEvent.SyncType.POIAREA, map.getProjection().getBoundingBox()));
                    v.removeOnLayoutChangeListener(this);
                }
            }

            private double round(double d) {
                d *= 100;
                d = Math.round(d);
                return d / 100;
            }
        });
        mapOverlays.setPoiRestAreaActive(restAreaActive);
        mapOverlays.setPoiFloraFaunaActive(floraFaunaActive);
        mapOverlays.setPoiRestaurantActive(restaurantActive);
        mapOverlays.setPoiViewActive(viewActive);
        mapView.setMapOverlays(this.mapOverlays);
        mapController.setCenter(centerOfMap);
        if (zoomLevel > 20 || zoomLevel < 1)
            mapController.setZoom(Defaults.ZOOM_STARTUP);
        else mapController.setZoom(zoomLevel);
    }

    /**
     * Initializes map overlays
     */
    private void initOverlays() {
        mapOverlays = new MyMapOverlays(getActivity(), mapView, this.searchMapController, this);
        // set position marker if last location is available
        if (!myLocationIsEnabled && lastKnownLocation != null
                && lastKnownLocation.getLatitude() != 0
                && lastKnownLocation.getLongitude() != 0) {
            mapOverlays.addPositionMarker(lastKnownLocation);
        }

    }

    /**
     * Initializes location toggler and sets icon of toggler to saved state in preferences
     *
     * @param view View: view of current fragment
     */
    private void initLocationToggler(View view) {
        ibLocationToggler = (ImageButton) view.findViewById(R.id.locationButton);
        displayMyLocationOnMap(myLocationIsEnabled);

        if (myLocationIsEnabled) {
            centerMapOnCurrentPosition();
            ibLocationToggler.setImageResource(R.drawable.ic_my_location_found_black_24dp);
        } else {
            ibLocationToggler.setImageResource(R.drawable.ic_my_location_disabled_black_24dp);
        }
        createTourButton.setEnabled(myLocationIsEnabled);

        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(ibLocationToggler);
        //toggle listener
        ibLocationToggler.setOnClickListener(v -> {
            if (myLocationIsEnabled) {
                // toggle to disabled
                myLocationIsEnabled = false;
                displayMyLocationOnMap(false);

                ibLocationToggler.setImageResource(R.drawable.ic_my_location_disabled_black_24dp);
                Toast.makeText(getActivity(), R.string.msg_follow_mode_disabled, Toast.LENGTH_SHORT).show();
            } else {
                // toggle to enabled
                myLocationIsEnabled = true;
                displayMyLocationOnMap(true);

                ibLocationToggler.setImageResource(R.drawable.ic_my_location_found_black_24dp);
                Toast.makeText(getActivity(), R.string.msg_follow_mode_enabled, Toast.LENGTH_SHORT).show();
                centerMapOnCurrentPosition();
            }
            createTourButton.setEnabled(myLocationIsEnabled);
        });

        //long click listener
        ibLocationToggler.setOnLongClickListener(view1 -> {
            Toast.makeText(getActivity(), R.string.msg_map_centered_on_long_click, Toast.LENGTH_SHORT).show();
            centerMapOnCurrentPosition();
            return true;
        });
    }

    /**
     * Initializes camera button
     *
     * @param view View: view of current fragment
     */
    private void initCameraButton(View view) {
        cameraButton = (FloatingActionButton) view.findViewById(R.id.takePictureButton);
        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(cameraButton);

        cameraButton.setOnClickListener(view1 -> {
            // prevent multiple clicks on button
            cameraButton.setEnabled(false);

            final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            //check if gps is activated and show corresponding toast
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                // buildAlertMessageNoGps();
                Toast.makeText(getActivity(), R.string.msg_camera_no_gps, Toast.LENGTH_SHORT).show();
                takePicture();
            } else {

                mapOverlays.getMyLocationNewOverlay().enableMyLocation();
                Toast.makeText(getActivity(), R.string.msg_camera_about_to_start, Toast.LENGTH_SHORT).show();
                mapOverlays.getMyLocationNewOverlay().runOnFirstFix(() -> {
                    lastKnownLocation = mapOverlays.getMyLocationNewOverlay().getMyLocation();
                    takePicture();
                });
            }
        });
    }

    private void takePicture() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
        Camera camera = new Camera(getActivity(), mapFragment);
        camera.start();
        imageFileName = camera.getImageName();
        photoPath = camera.getImagePath();
    }

    /**
     * Initializes layer button
     *
     * @param view View: view of current fragment
     */
    private void initLayerButton(View view) {
        layerButton = (ImageButton) view.findViewById(R.id.layerButton);
        layerButton.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int[] pos = new int[2];
            layerButton.getLocationOnScreen(pos);
            mapOverlays.setCompassPos(pos[0]+layerButton.getWidth()/2, pos[1]+layerButton.getHeight()*1.5f);
        });
        ImageButton closeBottomSheetButton = (ImageButton) view.findViewById(R.id.btn_close_bottom_sheet);

        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(layerButton);

        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // register behavior on clicked
        layerButton.setOnClickListener(view1 -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
               /*
                NestedScrollView layout = (NestedScrollView) view.findViewById(R.id.bottom_sheet);
                ViewGroup.LayoutParams params = layout.getLayoutParams();
                //params.height = 600;
                layout.setLayoutParams(params);
                */
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        closeBottomSheetButton.setOnClickListener(view1 -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));

        ibPoiLayer = (ImageButton) view.findViewById(R.id.poi_layer_button);
        boolean poiLayerActive = sharedPreferences.getBoolean(Constants.PREFERENCE_POI_LAYER_ACTIVE,true);
        ibPoiLayer.setSelected(poiLayerActive);
        showPoiOverlay(poiLayerActive);

        ibPoiLayer.setOnClickListener(v -> {
            boolean isSelected = ibPoiLayer.isSelected();
            showPoiOverlay(!isSelected);
            ibPoiLayer.setSelected(!isSelected);

            if (ibPoiLayer.isSelected()) {
                ibPoiLayer.setImageResource(R.drawable.ic_poi_white_24dp);
                ibPoiLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
            } else {
                ibPoiLayer.setImageResource(R.drawable.ic_poi_black_24dp);
                ibPoiLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
            }

            if(ibPoiLayer.isSelected()){
                poiTypeSelection.setVisibility(View.VISIBLE);
            } else {
                poiTypeSelection.setVisibility(View.GONE);
            }
        });

        boolean publicLayerActive = sharedPreferences.getBoolean(Constants.PREFERENCE_PUBLICTRANSPORT_LAYER_ACTIVE,true);
        ibPublicTransportLayer = (ImageButton) view.findViewById(R.id.public_transport_layer_button);
        ibPublicTransportLayer.setSelected(publicLayerActive);
        showPublicTransportOverlay(publicLayerActive);
        ibPublicTransportLayer.setOnClickListener(v -> showPublicTransportOverlay(!ibPublicTransportLayer.isSelected()));

        ibSacHutLayer = (ImageButton) view.findViewById(R.id.public_sac_layer_button);
        boolean sacHutLayerActive = sharedPreferences.getBoolean(Constants.PREFERENCE_SAC_LAYER_ACTIVE,true);
        ibSacHutLayer.setSelected(sacHutLayerActive);
        showSacHutOverlay(sacHutLayerActive);
        ibSacHutLayer.setOnClickListener(v -> showSacHutOverlay(!ibSacHutLayer.isSelected()));

        mapView.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        });
    }

    private void showPoiOverlay(boolean showOverlay) {
        if(!showOverlay) mapOverlays.disablePoiLayer();
        else mapOverlays.showPoiLayer();
    }

    private void showSacHutOverlay(boolean showOverlay) {
        ibSacHutLayer.setSelected(showOverlay);

        BoundingBox boundingBox = mapView.getProjection().getBoundingBox();
        GeoPoint point1 = new GeoPoint(boundingBox.getLatNorth(), boundingBox.getLonWest());
        GeoPoint point2 = new GeoPoint(boundingBox.getLatSouth(), boundingBox.getLonEast());

        if (mapView.getZoomLevel() > 10) {
            mapOverlays.showSacHutLayer(showOverlay, point1, point2);
        }
        mapView.setSacHutEnabledEnabled(showOverlay);

        if (showOverlay) {
            ibSacHutLayer.setImageResource(R.drawable.ic_home_24dp_white);
            ibSacHutLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
        } else {
            ibSacHutLayer.setImageResource(R.drawable.ic_home_24dp_black);
            ibSacHutLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
        }
    }


    /**
     * Shows the public transport overlay and changes the icon in the dialog
     */
    private void showPublicTransportOverlay(boolean showPublicTransportOverlay) {
        ibPublicTransportLayer.setSelected(showPublicTransportOverlay);
        mapView.setPublicTransportEnabled(showPublicTransportOverlay);
        mapOverlays.showPublicTransportLayer(showPublicTransportOverlay, (GeoPoint) mapView.getMapCenter());
        if (showPublicTransportOverlay) {
            ibPublicTransportLayer.setImageResource(R.drawable.ic_train_white_24dp);
            ibPublicTransportLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.primary_main));
        } else {
            ibPublicTransportLayer.setImageResource(R.drawable.ic_train_black_24dp);
            ibPublicTransportLayer.setBackgroundTintList(this.getActivity().getResources().getColorStateList(R.color.white));
        }
    }

    /**
     * Centers map on the current location of the user after refreshing his current position
     */
    private void centerMapOnCurrentPosition() {
        // enable myLocation if service is not running
        if (!mapOverlays.getMyLocationNewOverlay().isMyLocationEnabled()) {
            mapOverlays.getMyLocationNewOverlay().enableMyLocation();
        }
        // start async task and center map as soon the current location has been found
        mapOverlays.getMyLocationNewOverlay().runOnFirstFix(() -> {
            GeoPoint myLocation = mapOverlays.getMyLocationNewOverlay().getMyLocation();
            mapController.animateTo(myLocation);

            // stop location service if user has disabled myLocation
            if (!myLocationIsEnabled) {
                mapOverlays.getMyLocationNewOverlay().disableMyLocation();
            }
        });

        // set zoom to default zoom level when toggler is clicked for the first time
        setZoomToDefault(!locationTogglerHasBeenClicked);
    }

    /**
     * Sets zoom to default zoom level
     *
     * @param setZoom boolean: true if zoom level should be set to default
     */
    private void setZoomToDefault(boolean setZoom) {
        if (setZoom) {
            mapController.setZoom(Defaults.ZOOM_ENLARGED);
            mapOverlays.getMyLocationNewOverlay().enableFollowLocation();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Constants.PREFERENCE_BUTTON_LOCATION_CLICKED, true);
            locationTogglerHasBeenClicked = true;
            editor.apply();
        }
    }

    /**
     * Displays current location of user in map as person
     *
     * @param showMyLocation boolean: true if current location should be shown
     */
    private void displayMyLocationOnMap(boolean showMyLocation) {
        GeoPoint myLocation = mapOverlays.getMyLocationNewOverlay().getMyLocation();
        if (showMyLocation) {
            mapOverlays.removePositionMarker();

            mapOverlays.getMyLocationNewOverlay().enableMyLocation();
        } else {
            mapOverlays.addPositionMarker(myLocation);
            mapOverlays.getMyLocationNewOverlay().disableMyLocation();
        }
    }

    /**
     * Shows dialog after successfully returning from camera activity
     */
    private void dispatchPostFeedbackDialogFragment() {
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        // make sure that no other dialog is running
        Fragment prevFragment = getActivity().getFragmentManager().findFragmentByTag(Constants.EDIT_POI_DIALOG);
        if (prevFragment != null) fragmentTransaction.remove(prevFragment);
        fragmentTransaction.addToBackStack(null);

        PoiEditDialog dialog = PoiEditDialog.newInstance(imageFileName, lastKnownLocation);
        dialog.show(fragmentTransaction, Constants.EDIT_POI_DIALOG);
    }
    /**
     * Generates a options menu in the toolbar and inflates it. Menu is specific for this Fragment and is
     * only shown in this toolbar.
     *
     * @param menu     Menu: options menu
     * @param inflater MenuInflater: menu inflater of options menu
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); // makes sure that the menu was not inflated yet
        inflater.inflate(R.menu.map_fragment_layer_menu, menu);
        //initSearchView(menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //getActivity().invalidateOptionsMenu();
        if(menu.findItem(R.id.action_search) != null) {
            SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            searchView.setSuggestionsAdapter(mAdapter);
            searchView.setIconifiedByDefault(false);
            // Getting selected (clicked) item suggestion
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {
                    triggerHashtagSearch(position, null);
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    triggerHashtagSearch(position, null);
                    return true;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (s.startsWith("#")) {
                        triggerHashtagSearch(-1, s.substring(1));
                    } else {
                        callSearch(s);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    if (s.trim().startsWith("#") && s.length() >= 2) {
                        mapView.setHashTagEnabled(true);
                        searchMapController.suggestHashtags(s.substring(1), controllerEvent -> {
                            if (controllerEvent.getModel() != null && controllerEvent.getModel().size() != 0) {
                                hashTagSearchSuggestions = controllerEvent.getModel();
                            } else {
                                hashTagSearchSuggestions.clear();
                            }
                            populateAdapter(s.substring(1));

                        });
                    } else {
                        mapView.setHashTagEnabled(false);
                        DatabaseController.getInstance().sync(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA, mapView.getProjection().getBoundingBox()));
                        if (hashTagSearchSuggestions != null) {
                            hashTagSearchSuggestions.clear();
                        }
                        if (s.length() >= 1) {
                            populateAdapter(s.substring(1));
                        }
                    }
                    return true;
                }

                public void callSearch(String query) {
                    try {
                        searchMapController.searchPlace(query, 1, controllerEvent -> {
                            List<MapSearchResult> resultList = (List<MapSearchResult>) controllerEvent.getModel();
                            if (!resultList.isEmpty()) {
                                MapSearchResult firstResult = resultList.get(0);
                                GeoPoint geoPoint = new GeoPoint(firstResult.getLatitude(), firstResult.getLongitude());
                                if (!firstResult.getPolygon().isEmpty() && firstResult.getPolygon().get(0) != null) {
                                    mapOverlays.clearPolylines();

                                    double minLat = 9999;
                                    double maxLat = -9999;
                                    double minLong = 9999;
                                    double maxLong = -9999;

                                    for (ArrayList<GeoPoint> polygon : firstResult.getPolygon()) {
                                        mapOverlays.addPolyline(polygon);

                                        for (GeoPoint point : polygon) {
                                            if (point.getLatitude() < minLat)
                                                minLat = point.getLatitude();
                                            if (point.getLatitude() > maxLat)
                                                maxLat = point.getLatitude();
                                            if (point.getLongitude() < minLong)
                                                minLong = point.getLongitude();
                                            if (point.getLongitude() > maxLong)
                                                maxLong = point.getLongitude();
                                        }
                                    }

                                    BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);
                                    mapView.zoomToBoundingBox(boundingBox.increaseByScale(1.1f), true);
                                } else {
                                    mapController.setZoom(Defaults.ZOOM_SEARCH);
                                    mapController.animateTo(geoPoint);
                                    mapOverlays.addFocusedPositionMarker(geoPoint);
                                }
                            } else {
                                Toast.makeText(getActivity(), R.string.map_nothing_found, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), R.string.map_nothing_found, Toast.LENGTH_SHORT).show();
                    }
                    searchView.clearFocus();
                }
            });
        }
    }


    private void populateAdapter(String query) {
        c = new MatrixCursor(new String[]{BaseColumns._ID, "hashTag"});
        int length = this.hashTagSearchSuggestions.size() > 6 ? 6 : this.hashTagSearchSuggestions.size();
        for (int i = 0; i < length; i++) {
            if (this.hashTagSearchSuggestions.get(i).getTag().toLowerCase().startsWith(query.toLowerCase())) {
                c.addRow(new Object[]{i, this.hashTagSearchSuggestions.get(i).getTag()});
            }
        }

        mAdapter.changeCursor(c);
    }

    private void triggerHashtagSearch(int position, String query) {
        String element;
        if (query != null) { // use the input (search directly)
            element = query;
        } else { // take input from suggestions (search via searching suggestion)
            element = c.getString(1);
        }

        BoundingBox boundingBox = mapView.getProjection().getBoundingBox();
        GeoPoint point1 = new GeoPoint(boundingBox.getLatSouth(), boundingBox.getLonWest());
        GeoPoint point2 = new GeoPoint(boundingBox.getLatNorth(), boundingBox.getLonEast());

        for (HashtagResult hashtagResult : hashTagSearchSuggestions) {
            if (hashtagResult.getTag().equals(element)) {
                searchMapController.serachHashtag(hashtagResult.getHashId(), point2, point1, controllerEvent -> {
                    List<Poi> hashtagPoiList = controllerEvent.getModel();

                    // hide poi layer so that hashtagsearch results can be displayed
                    showPoiOverlay(false);

                    // hide poi layer so that hashtagsearch results can be displayed
                    mapOverlays.updateHashtagPoiLayer(hashtagPoiList);
                    if (hashtagPoiList.size() == 0) {
                        Toast.makeText(getActivity(), R.string.hashtag_search_nothing_found, Toast.LENGTH_LONG).show();
                    } else {
                        // close keyboard
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                });
                break;
            }
        }
    }

    public void showInformationBottomSheet(boolean toggleBottomsheet, String text) {
        if (toggleBottomsheet) {
            informationBottomSheetString.setText(text);
            informationBottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            informationBottomSheet.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }


    /************************************************* Tour Tracking ***************************************************/

    private boolean startTourTracking() {
        PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);

        if (pm != null && pm.isPowerSaveMode() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(getActivity(), R.string.create_tour_disable_battery_save_mode, Toast.LENGTH_LONG).show();
            return false;
        } else {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(createTourReceiver, new IntentFilter(Constants.CREATE_TOUR_INTENT));
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateTrackingOverlayReceiver, new IntentFilter(Constants.CREATE_TOUR_UPDATE_MYOVERLAY));
            getActivity().startService(createTourIntent);
            return true;
        }

    }



    private void stoptTourTracking() {
        getActivity().stopService(createTourIntent);
        createTourButton.setImageResource(R.drawable.ic_track_start_3);
        createTourButton.setColorNormalResId(R.color.primary_main);
        creatingTourInformation.setVisibility(View.GONE);
    }

    /**
     * Shows dialog after successfully collecting geoPoints
     *
     * @param track the collected geopoints from the tracked tour
     */
    private void openCreateTourDialog(ArrayList<GeoPoint> track) {
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        // make sure that no other dialog is running
        Fragment prevFragment = getActivity().getFragmentManager().findFragmentByTag(Constants.CREATE_TOUR_DIALOG);
        if (prevFragment != null) fragmentTransaction.remove(prevFragment);
        fragmentTransaction.addToBackStack(null);

        CreateTourDialog dialog = CreateTourDialog.newInstance(track);
        dialog.show(fragmentTransaction, Constants.CREATE_TOUR_DIALOG);
    }

    private BroadcastReceiver createTourReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle args = intent.getBundleExtra(Constants.CREATE_TOUR_BUNDLE);
            ArrayList<GeoPoint> track = (ArrayList<GeoPoint>) args.getSerializable(Constants.CREATE_TOUR_TRACK);
            if (validateCreatedTour(track)) {
                openCreateTourDialog(track);
            } else {
                Toast.makeText(getActivity(), R.string.create_tour_nothing_tracked, Toast.LENGTH_SHORT).show();
            }
            mapOverlays.clearTrackingOverlay();
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(createTourReceiver);
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateTrackingOverlayReceiver);
        }
    };

    private boolean validateCreatedTour(ArrayList<GeoPoint> track) {
        if(track == null){
            return false;
        }

        int maxDistanceBetweenTwoPoints = 0;

        for(int i=1; i< track.size() -1 ; i++){
            if(track.get(i).distanceTo(track.get(i + 1)) > maxDistanceBetweenTwoPoints){
                maxDistanceBetweenTwoPoints = track.get(i).distanceTo(track.get(i + 1));
            }

        }

        return track.size() < 6000 & track.size() > 30 && maxDistanceBetweenTwoPoints < 250;
    }

    private BroadcastReceiver updateTrackingOverlayReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle args = intent.getBundleExtra(Constants.CREATE_TOUR_BUNDLE);
            GeoPoint trackedGeoPoint = (GeoPoint) args.getSerializable(Constants.CREATE_TOUR_ADDING_GEOPOINT);
            if (trackedGeoPoint != null) {
                mapOverlays.addItemToTrackingOverlay(trackedGeoPoint);
            } else { // No GeoPoint was sent, maybe the whole tour was sent
                ArrayList<GeoPoint> trackedGeoPoints = (ArrayList<GeoPoint>) args.getSerializable(Constants.CREATE_TOUR_ADDING_GEOPOINTS);
                if (trackedGeoPoints != null) {
                    mapOverlays.refreshTrackingOverlay(trackedGeoPoints);
                }
            }

        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public MapView getMapView(){
        return mapView;
    }

}
