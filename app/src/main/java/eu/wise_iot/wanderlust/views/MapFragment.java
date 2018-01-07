package eu.wise_iot.wanderlust.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.LocationManager;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.IOException;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.models.Old.Camera;
import eu.wise_iot.wanderlust.views.animations.StyleBehavior;
import eu.wise_iot.wanderlust.views.dialog.EditPoiDialog;

/**
 * MapFragment: The Fragment that contains the map view, map functionality and buttons.
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class MapFragment extends Fragment {
    private static final String TAG = "MapFragment";
    public static String photoPath;
    private static String imageFileName;
    // preferences and default settings
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean locationTogglerHasBeenClicked;
    private boolean myLocationIsEnabled;
    private int zoomLevel;
    private GeoPoint centerOfMap;
    private GeoPoint lastKnownLocation;
    //private MapView mapView;
    private WanderlustMapView mapView;
    private IMapController mapController;
    private MyMapOverlays mapOverlays;
    private Camera camera;
    private ImageButton locationToggler;
    private ImageButton cameraButton;
    private ImageButton layerButton;
    private ImageButton staliteTypeButton;
    private ImageButton defaultTypeButton;
    private ImageButton terrainTypeButton;
    private View bottomSheet;
    private SearchView searchView;
    private MapController searchMapController;


    // bottom sheet
    private ImageButton poiLayerButton;

    /**
     * Static instance constructor.
     *
     * @return Fragment: MapFragment
     */
    public static MapFragment newInstance() {

        Bundle args = new Bundle();
        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchMapController = new MapController(this);
        setHasOptionsMenu(true);
        loadPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initMap(view);
        initOverlays();
        initMapController();
        DatabaseController.register(mapOverlays);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DatabaseController.unregister(mapOverlays);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLocationToggler(view);
        initCameraButton(view);
        initLayerButton(view);
        initMapTypeButton(view);
    }

    private void initMapTypeButton(View view) {
        staliteTypeButton = (ImageButton) view.findViewById(R.id.map_satelite_type);
        defaultTypeButton = (ImageButton) view.findViewById(R.id.map_default_type);
        terrainTypeButton = (ImageButton) view.findViewById(R.id.map_terrain_type);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        defaultTypeButton.setBackground(getActivity().getDrawable(R.drawable.map_icon_selected_border));

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
            staliteTypeButton.setBackground(getActivity().getDrawable(R.drawable.map_icon_selected_border));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });

        defaultTypeButton.setOnClickListener(e -> {
            ITileSource tileSource = new XYTileSource("OpenTopoMap", 0, 20, 256, ".png",
                    new String[]{"https://opentopomap.org/"});
            mapView.setTileSource(tileSource);
            staliteTypeButton.setBackground(null);
            terrainTypeButton.setBackground(null);
            defaultTypeButton.setBackground(getActivity().getDrawable(R.drawable.map_icon_selected_border));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        });

        terrainTypeButton.setOnClickListener(e -> {
            ITileSource tileSource = new XYTileSource("Stamen", 0, 20, 256, ".png",
                    new String[]{"http://c.tile.stamen.com/terrain/"});
            mapView.setTileSource(tileSource);
            staliteTypeButton.setBackground(null);
            defaultTypeButton.setBackground(null);
            terrainTypeButton.setBackground(getActivity().getDrawable(R.drawable.map_icon_selected_border));
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    /**
     * Initializes the search bar on the top of the application
     *
     * @param menu The menu with the searchbar, which needs to be initialized
     */
    public void initSearchView(Menu menu) {
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: SEARCH SUGGETION
                return true;
            }

            public void callSearch(String query) {
                try {
                    searchMapController.searchPlace(query, 1, new FragmentHandler() {
                        @Override
                        public void onResponse(ControllerEvent controllerEvent) {
                            List<Address> resultList = (List<Address>) controllerEvent.getModel();
                            if (!resultList.isEmpty()) {
                                Address firstResult = resultList.get(0);
                                GeoPoint geoPoint = new GeoPoint(firstResult.getLatitude(), firstResult.getLongitude());
                                mapController.setZoom(Defaults.ZOOM_SEARCH);
                                mapController.animateTo(geoPoint);
                                mapOverlays.addFocusedPositionMarker(geoPoint);
                            } else {
                                Toast.makeText(getActivity(), R.string.map_nothing_found, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (IOException e) {
                    Toast.makeText(getActivity(), R.string.map_nothing_found, Toast.LENGTH_SHORT).show();
                }
                searchView.clearFocus();
            }

        });
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
        savePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPreferences();
    }

    @Override
    public void onStop() {
        super.onStop();
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
        menu.clear(); // makes shure that the menu was not inflated yet
        inflater.inflate(R.menu.map_fragment_layer_menu, menu);

        initSearchView(menu);

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
        locationTogglerHasBeenClicked = sharedPreferences.getBoolean(Constants.BUTTON_LOCATION_CLICKED, false);
        myLocationIsEnabled = sharedPreferences.getBoolean(Constants.MY_LOCATION_ENABLED, myLocationIsEnabled);
    }

    /**
     * Saves user preferences of map settings in shared preferences
     */
    private void savePreferences() {
        int lastZoomLevel = mapView.getZoomLevel();
        double lastMapCenterLat = mapView.getMapCenter().getLatitude();
        double lastMapCenterLon = mapView.getMapCenter().getLongitude();
        editor = sharedPreferences.edit();
        editor.putInt(Constants.LAST_ZOOM_LEVEL, lastZoomLevel);
        editor.putLong(Constants.LAST_MAP_CENTER_LAT, Double.doubleToRawLongBits(lastMapCenterLat));
        editor.putLong(Constants.LAST_MAP_CENTER_LON, Double.doubleToRawLongBits(lastMapCenterLon));
        editor.putLong(Constants.LAST_POS_LAT, Double.doubleToLongBits(lastKnownLocation.getLatitude()));
        editor.putLong(Constants.LAST_POS_LON, Double.doubleToLongBits(lastKnownLocation.getLongitude()));
        editor.putBoolean(Constants.MY_LOCATION_ENABLED, myLocationIsEnabled);
        editor.apply();
    }


    /**
     * Initializes map view
     *
     * @param view View: view of current fragment
     */
    private void initMap(View view) {
        mapView = (WanderlustMapView) view.findViewById(R.id.mapView);
        //https://osm.rrze.fau.de/
//        ITileSource tileSource = new XYTileSource("RRZE",
//                0, 19, 512, ".png",
//                new String[] { "http://osm.rrze.fau.de/osmhd/" });

        ITileSource tileSource = new XYTileSource("OpenTopoMap", 0, 20, 256, ".png",
                new String[]{"https://opentopomap.org/"});
        mapView.setTileSource(tileSource);


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
                    DatabaseController.sync(new DatabaseEvent<BoundingBox>(DatabaseEvent.SyncType.POIAREA, map.getProjection().getBoundingBox()));
                    v.removeOnLayoutChangeListener(this);
                }
            }

            private double round(double d) {
                d *= 100;
                d = Math.round(d);
                return d / 100;
            }
        });
        mapController.setCenter(centerOfMap);
        if (zoomLevel > 20 || zoomLevel < 1)
            mapController.setZoom(Defaults.ZOOM_STARTUP);
        else mapController.setZoom(zoomLevel);
    }

    /**
     * Initializes map overlays
     */
    private void initOverlays() {
        mapOverlays = new MyMapOverlays(getActivity(), mapView);
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
        locationToggler = (ImageButton) view.findViewById(R.id.locationButton);
        displayMyLocationOnMap(myLocationIsEnabled);

        if (myLocationIsEnabled) {
            centerMapOnCurrentPosition();
            locationToggler.setImageResource(R.drawable.ic_my_location_found_black_24dp);
        } else {
            locationToggler.setImageResource(R.drawable.ic_my_location_disabled_black_24dp);
        }

        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(locationToggler);
        //toggle listener
        locationToggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocationIsEnabled) {
                    // toggle to disabled
                    myLocationIsEnabled = false;
                    displayMyLocationOnMap(false);

                    locationToggler.setImageResource(R.drawable.ic_my_location_disabled_black_24dp);
                    Toast.makeText(getActivity(), R.string.msg_follow_mode_disabled, Toast.LENGTH_SHORT).show();
                } else {
                    // toggle to enabled
                    myLocationIsEnabled = true;
                    displayMyLocationOnMap(true);

                    locationToggler.setImageResource(R.drawable.ic_my_location_found_black_24dp);
                    Toast.makeText(getActivity(), R.string.msg_follow_mode_enabled, Toast.LENGTH_SHORT).show();
                    centerMapOnCurrentPosition();
                }
            }
        });

        //long click listener
        locationToggler.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getActivity(), R.string.msg_map_centered_on_long_click, Toast.LENGTH_SHORT).show();
                centerMapOnCurrentPosition();
                return true;
            }
        });
    }

    /**
     * Initializes camera button
     *
     * @param view View: view of current fragment
     */
    private void initCameraButton(View view) {
        cameraButton = (ImageButton) view.findViewById(R.id.takePictureButton);
        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(cameraButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    mapOverlays.getMyLocationNewOverlay().runOnFirstFix(new Runnable() {
                        @Override
                        public void run() {
                            lastKnownLocation = mapOverlays.getMyLocationNewOverlay().getMyLocation();
                            takePicture();
                        }
                    });
                }
            }
        });
    }

    private void takePicture() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
        camera = new Camera(getActivity(), mapFragment);
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

        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(layerButton);

        View bottomSheet = view.findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // register behavior on clicked
        layerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
            }
        });

        poiLayerButton = (ImageButton) view.findViewById(R.id.poi_layer_button);
        showPoiOverlay(true);

        poiLayerButton.setOnClickListener(v -> {
            boolean toggleLayer = !poiLayerButton.isSelected();
            showPoiOverlay(toggleLayer);
        });
    }

    private void showPoiOverlay(boolean showOverlay) {
        poiLayerButton.setSelected(showOverlay);
        mapOverlays.showPoiLayer(showOverlay);
        if (showOverlay) {
            poiLayerButton.setImageResource(R.drawable.ic_poi_selected_24dp);
        } else {
            poiLayerButton.setImageResource(R.drawable.ic_poi_black_24dp);
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
        mapOverlays.getMyLocationNewOverlay().runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                GeoPoint myLocation = mapOverlays.getMyLocationNewOverlay().getMyLocation();
                mapController.animateTo(myLocation);

                // stop location service if user has disabled myLocation
                if (!myLocationIsEnabled) {
                    mapOverlays.getMyLocationNewOverlay().disableMyLocation();
                }
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
            editor.putBoolean(Constants.BUTTON_LOCATION_CLICKED, true);
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
        Fragment prevFragment = getActivity().getFragmentManager().findFragmentByTag(Constants.CREATE_FEEDBACK_DIALOG);
        if (prevFragment != null) fragmentTransaction.remove(prevFragment);
        fragmentTransaction.addToBackStack(null);

        EditPoiDialog dialog = EditPoiDialog.newInstance(imageFileName, lastKnownLocation);
        Log.d(TAG, "lastKnownLocation: " + lastKnownLocation);
        dialog.show(fragmentTransaction, Constants.CREATE_FEEDBACK_DIALOG);
    }
}

