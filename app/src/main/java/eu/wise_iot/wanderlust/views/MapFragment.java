package eu.wise_iot.wanderlust.views;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.io.File;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.views.dialog.CreateFeedbackDialog;
import eu.wise_iot.wanderlust.models.Camera;
import eu.wise_iot.wanderlust.models.StyleBehavior;

/**
 * MapFragment:
 * @author Fabian Schwander
 * @license MIT
 */
public class MapFragment extends Fragment{
    private static final String TAG = "MapFragment";

    // preferences and default settings
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean locationTogglerHasBeenClicked;
    private boolean myLocationIsEnabled;
    private int zoomLevel;
    private GeoPoint centerOfMap;
    private GeoPoint lastKnownLocation;

    private MapView mapView;
    private IMapController mapController;
    private MyMapOverlays mapOverlays;

    private Camera camera;
    private static String imageFileName;
    private static String photoPath;

    private ImageButton locationToggler;
    private ImageButton cameraButton;
    private ImageButton layerButton;
    private Button centerMapOnPoiButton;

    /**
     * Static instance constructor. Used after a Picture was taken and image file name is passed to Mapfragment.
     *
     * @return MapFragment
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
        setHasOptionsMenu(true);
        loadPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initMap(view);
        initOverlays();
        initMapController();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLocationToggler(view);
        initCameraButton(view);
        initLayerButton(view);
        initCenterMapOnPoiButton(view);
    }

    @Override
    public void onStart() {
        super.onStart();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feedback_overlay:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mapOverlays.showOverlay(false);
                } else {
                    item.setChecked(true);
                    mapOverlays.showOverlay(true);
                }
                break;
//            case R.id.trails_overlay: // FIXME: UNCOMMENTED FOR RELEASE 0.1
//                // TODO: Add actions
//                break;
//            case R.id.heatmap_overlay:
//                // TODO: Add actions
//                break;
//            case R.id.public_transport_overlay:
//                // TODO: Add actions
//                break;
//            case R.id.restaurants_overlay:
//                // TODO: Add actions
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // photo intent finished and image saved
        if (requestCode == Constants.TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            dispatchPostFeedbackDialogFragment();
        }
        // photo intent aborted
        if (requestCode == Constants.TAKE_PHOTO && resultCode != Activity.RESULT_OK) {
            File file = new File(photoPath);
            file.delete();
        }
    }

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

    private void initMap(View view) {
        mapView = (MapView) view.findViewById(R.id.mapView);
        ITileSource tileSource = new XYTileSource("OpenTopoMap", 0, 20, 256, ".png",
                new String[]{"https://opentopomap.org/"});
        mapView.setTileSource(tileSource);
        mapView.setTilesScaledToDpi(true);
        mapView.setMultiTouchControls(true);
    }

    private void initMapController() {
        mapController = mapView.getController();
        mapController.setCenter(centerOfMap);
        if (zoomLevel > 20 || zoomLevel < 1)
            mapController.setZoom(Defaults.ZOOM_STARTUP);
        else mapController.setZoom(zoomLevel);
    }

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
     * initializes location toggler
     * @param view
     */
    private void initLocationToggler(View view) {
        locationToggler = (ImageButton) view.findViewById(R.id.locationButton);
        displayMyLocationOnMap(myLocationIsEnabled);

        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(locationToggler);
        //set as default on
        locationToggler.setTag("on");
        //toggle listener
        locationToggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationToggler.getTag().toString().trim().equals("on")) {
                    locationToggler.setTag("off");
                    locationToggler.setImageResource(R.drawable.ic_location_searching_black_24dp);
                    Toast.makeText(getActivity(), R.string.msg_follow_mode_enabled, Toast.LENGTH_SHORT).show();
                    myLocationIsEnabled = true;
                } else if (locationToggler.getTag().toString().trim().equals("off")) {
                    locationToggler.setTag("on");
                    locationToggler.setImageResource(R.drawable.ic_location_disabled_black_24dp);
                    Toast.makeText(getActivity(), R.string.msg_follow_mode_disabled, Toast.LENGTH_SHORT).show();
                    myLocationIsEnabled = false;
                }
                displayMyLocationOnMap(myLocationIsEnabled);
                setZoomToDefault(!locationTogglerHasBeenClicked);
            }
        });

        //long click listener
        locationToggler.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(getActivity(), R.string.msg_map_centered_on_long_click, Toast.LENGTH_SHORT).show();
                centerMapOnCurrentPosition();
                locationToggler.setImageResource(R.drawable.ic_my_location_black_24dp);
                return true;
            }
        });
    }

    /**
     * initializes camera button
     * @param view
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
                Toast.makeText(getActivity(), R.string.msg_camera_about_to_start, Toast.LENGTH_SHORT).show();

                if (!mapOverlays.getMyLocationNewOverlay().isMyLocationEnabled()) {
                    mapOverlays.getMyLocationNewOverlay().enableMyLocation();
                }
                mapOverlays.getMyLocationNewOverlay().runOnFirstFix(new Runnable() {
                    @Override
                    public void run() {
                        lastKnownLocation = mapOverlays.getMyLocationNewOverlay().getMyLocation();
                        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentByTag(Constants.MAP_FRAGMENT);
                        camera = new Camera(getActivity(), mapFragment);
                        camera.start();
                        imageFileName = camera.getImageName();
                        photoPath = camera.getImagePath();
                    }
                });
            }
        });
    }
    /**
     * initializes layer button
     * @param view
     */
    private void initLayerButton(View view) {
        //get instance
        layerButton = (ImageButton) view.findViewById(R.id.layerButton);
        //register behavior on touched
        StyleBehavior.buttonEffectOnTouched(layerButton);
        //register behavior on clicked
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO show here layer selection
            }
        });
    }

    private void initCenterMapOnPoiButton(View view) { // FIXME: UNCOMMENTED FOR RELEASE 0.1
//        centerMapOnPoiButton = (Button) view.findViewById(R.id.centerMapOnPOIButton);
//        centerMapOnPoiButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                centerMapOnPoi();
//            }
//        });
    }

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
//                if (!myLocationIsEnabled) mapOverlays.addPositionMarker(myLocation); // FIXME: add position marker in ui thread after position locatet without enabling myLocation
            }
        });
        // zoom in on toggler first dime clicked
        setZoomToDefault(!locationTogglerHasBeenClicked);
    }

    private void centerMapOnPoi() {
        Toast.makeText(getActivity(), R.string.msg_button_for_dev_only, Toast.LENGTH_LONG).show();
        mapController.setZoom(Defaults.ZOOM_ENLARGED);
        mapController.animateTo(Defaults.GEO_POINT_POI);
        //locationToggler.setChecked(false);
    }

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

    private void dispatchPostFeedbackDialogFragment() {
        FragmentTransaction fragmentTransaction = getActivity().getFragmentManager().beginTransaction();
        // make sure that no other dialog is running
        Fragment prevFragment = getActivity().getFragmentManager().findFragmentByTag(Constants.CREATE_FEEDBACK_DIALOG);
        if (prevFragment != null) fragmentTransaction.remove(prevFragment);
        fragmentTransaction.addToBackStack(null);

        CreateFeedbackDialog dialog = CreateFeedbackDialog.newInstance(imageFileName, lastKnownLocation);
        Log.d(TAG, "lastKnownLocation: " + lastKnownLocation);
        dialog.show(fragmentTransaction, Constants.CREATE_FEEDBACK_DIALOG);
    }
}