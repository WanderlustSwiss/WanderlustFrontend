package eu.wise_iot.wanderlust.views;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;

public class WanderlustMapView extends MapView {

    private boolean initMap;

    public WanderlustMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WanderlustMapView(Context context) {
        super(context);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean result = super.dispatchTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            DatabaseController.getInstance().sync(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA, this.getProjection().getBoundingBox()));
        }
        return result;
    }
}
