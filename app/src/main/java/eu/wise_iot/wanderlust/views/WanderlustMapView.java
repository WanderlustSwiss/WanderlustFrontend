package eu.wise_iot.wanderlust.views;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.osmdroid.tileprovider.MapTileProviderBase;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;

public class WanderlustMapView extends MapView {

    public WanderlustMapView(Context context, String apiKey) {
        super(context);
    }

    public WanderlustMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs);
    }

    public WanderlustMapView(Context context, MapTileProviderBase tileProvider, Handler tileRequestCompleteHandler, AttributeSet attrs, boolean hardwareAccelerated) {
        super(context, tileProvider, tileRequestCompleteHandler, attrs, hardwareAccelerated);
    }

    public WanderlustMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WanderlustMapView(Context context) {
        super(context);
    }

    public WanderlustMapView(Context context, MapTileProviderBase aTileProvider) {
        super(context, aTileProvider);
    }

    public WanderlustMapView(Context context, MapTileProviderBase aTileProvider, Handler tileRequestCompleteHandler) {
        super(context, aTileProvider, tileRequestCompleteHandler);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        boolean result = super.dispatchTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            BoundingBox box = this.getProjection().getBoundingBox();
            DatabaseController.sync(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA, box));
        }
        return result;
    }
}
