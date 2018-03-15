package eu.wise_iot.wanderlust.views;


import android.content.Context;
import android.graphics.Rect;
import android.support.design.widget.BottomSheetBehavior;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;

public class WanderlustMapView extends MapView {

    private GeoPoint centerOfPublicTransportOverlay = new GeoPoint(0.0, 0.0);
    private boolean publicTransportEnabled;
    private MyMapOverlays mapOverlays;

    private BottomSheetBehavior bottomSheetBehavior;
    private View bottomSheet;

    public void setBottomSheetClosingComponents(View bottomSheet, BottomSheetBehavior bottomSheetBehavior) {
        this.bottomSheet = bottomSheet;
        this.bottomSheetBehavior = bottomSheetBehavior;
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

    public void setMapOverlays(MyMapOverlays mapOverlays) {
        this.mapOverlays = mapOverlays;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = super.dispatchTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            DatabaseController.getInstance().sync(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA, this.getProjection().getBoundingBox()));

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
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN && bottomSheet != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            Rect outRect = new Rect();
            bottomSheet.getGlobalVisibleRect(outRect);

            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY()))
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }


        return result;
    }
}
