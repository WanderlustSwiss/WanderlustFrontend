package eu.wise_iot.wanderlust.views;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;


import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;

public class WanderlustMapView extends MapView {

    //private WanderlustMapController controller;
    private boolean initMap;

    public WanderlustMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //this.controller = new MapController(this);
    }

    public WanderlustMapView(Context context) {
        super(context);
    }


//    @Override
//    public IMapController getController() {
//        return this.controller;
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        boolean result = super.dispatchTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            DatabaseController.sync(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA, this.getProjection().getBoundingBox()));
        }
        return result;
    }

//    @Override
//    protected void setProjection(Projection p){
//        super.setProjection(p);
//    }
}
