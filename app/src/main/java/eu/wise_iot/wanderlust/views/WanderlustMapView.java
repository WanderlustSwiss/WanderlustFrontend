package eu.wise_iot.wanderlust.views;


import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.BottomSheetBehavior;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.MotionEventListener;

public class WanderlustMapView extends MapView implements RotationGestureDetector.OnRotationGestureListener {

    private GeoPoint centerOfPublicTransportOverlay = new GeoPoint(0.0, 0.0);

    private boolean publicTransportEnabled;
    private boolean sacHutEnabled;
    private boolean hashTagEnabled;

    private final RotationGestureDetector rotationGestureDetector = new RotationGestureDetector(this);
    private MyMapOverlays mapOverlays;

    // Layer BottomSheet
    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;

    private WanderlustCompassOverlay wanderlustCompassOverlay;

    private final ArrayList<MotionEventListener<WanderlustMapView>> motionEventListenerList = new ArrayList<>();

    public void setBottomSheetClosingComponents(View bottomSheet, BottomSheetBehavior bottomSheetBehavior) {
        this.bottomSheet = bottomSheet;
        this.bottomSheetBehavior = bottomSheetBehavior;
    }

    public void addObserver(MotionEventListener<WanderlustMapView> motionEventListener){
        this.motionEventListenerList.add(motionEventListener);
    }



    public WanderlustMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WanderlustMapView(Context context) {
        super(context);
    }

    public void setPublicTransportEnabled(boolean publicTransportEnabled) {
        this.publicTransportEnabled = publicTransportEnabled;
    }

    public void setHashTagEnabled(boolean hashTagEnabled){
        this.hashTagEnabled = hashTagEnabled;
    }

    public void setSacHutEnabledEnabled(boolean sacHutEnabled) {
        this.sacHutEnabled = sacHutEnabled;
    }

    public void setMapOverlays(MyMapOverlays mapOverlays) {
        this.mapOverlays = mapOverlays;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = super.dispatchTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {

            if(!hashTagEnabled) {
                DatabaseController.getInstance().sync(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA, this.getProjection().getBoundingBox()));
            }

            // Update public transport
            if (getZoomLevel() > 14 && publicTransportEnabled) {
                if (centerOfPublicTransportOverlay.distanceTo(getMapCenter()) > 1000) {
                    centerOfPublicTransportOverlay = (GeoPoint) getMapCenter();
                    if (mapOverlays != null) {
                        mapOverlays.showPublicTransportLayer(true, centerOfPublicTransportOverlay);
                    }
                }
            } else {
                mapOverlays.showPublicTransportLayer(false, null);
                centerOfPublicTransportOverlay = new GeoPoint(0.0, 0.0);
            }

            if (BuildConfig.DEBUG) Log.d("ZOOOOOOOOM", String.valueOf(getZoomLevel()));

            // Update SAC Hut Overlay
            if (sacHutEnabled) {
                if (getZoomLevel() > 9) {
                    BoundingBox boundingBox = getProjection().getBoundingBox();
                    GeoPoint point1 = new GeoPoint(boundingBox.getLatNorth(), boundingBox.getLonWest());
                    GeoPoint point2 = new GeoPoint(boundingBox.getLatSouth(), boundingBox.getLonEast());

                    if (mapOverlays != null) {
                        mapOverlays.showSacHutLayer(true, point1, point2);
                    }
                } else {
                    mapOverlays.showSacHutLayer(false, null, null);
                }
            }
        }


        // Close layer dialog if expanded
        if (event.getAction() == MotionEvent.ACTION_DOWN && bottomSheet != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            Rect outRect = new Rect();
            bottomSheet.getGlobalVisibleRect(outRect);

            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }

        for (MotionEventListener<WanderlustMapView> motionEventListener : motionEventListenerList){
            motionEventListener.update(this, event);
        }


        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        rotationGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        float angle = rotationDetector.getAngle();
        wanderlustCompassOverlay.setLastKnownAngle(angle);
        this.setMapOrientation(-angle);
        if (BuildConfig.DEBUG) Log.d("RotationGestureDetector", "Rotation: " + Float.toString(angle));
    }


    public void setWanderlustCompassOverlay(WanderlustCompassOverlay wanderlustCompassOverlay) {
        this.wanderlustCompassOverlay = wanderlustCompassOverlay;
    }
}
