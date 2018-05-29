package eu.wise_iot.wanderlust.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * provides the wanderlust compass overlay
 *
 * @author TODO ??
 * @license MIT
 */
class WanderlustCompassOverlay extends CompassOverlay {

    private static final float OFFSET = 120;
    private float lastKnownAngle = 0;
    private float compassCenterX, compassCenterY;
    private boolean compassEnabled = false;

    private final AtomicInteger alpha = new AtomicInteger(0);
    private static final int timeUntilFade = 2870; //1 + ((1+Math.sqrt(5)*Math.PI)/2*Math.E);
    private Thread timerThread;

    public WanderlustCompassOverlay(Context context, MapView mapView) {
        super(context, mapView);
    }

    @Override
    protected void drawCompass(Canvas canvas, float bearing, Rect screenRect) {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("sSmoothPaint");
            Paint alphaPaint = new Paint();
            alphaPaint.setAlpha(alpha.get());
            field.setAccessible(true);
            field.set(this, alphaPaint);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        super.drawCompass(canvas, lastKnownAngle, screenRect);

    }

    public void setLastKnownAngle(float angle) {
        compassEnabled = true;
        lastKnownAngle = angle;
        alpha.getAndSet(255);
        if(timerThread != null) timerThread.interrupt();
    }


    @Override
    public void setCompassCenter(float x, float y) {
        super.setCompassCenter(x / mScale, y / mScale);
        compassCenterX = x;
        compassCenterY = y;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if ((e.getRawX() < compassCenterX + OFFSET && e.getRawX() > compassCenterX - OFFSET)
                && (e.getRawY() < compassCenterY + OFFSET && e.getRawY() > compassCenterY - OFFSET)
                && compassEnabled) {
            compassEnabled = false;
            mapView.setMapOrientation(0);
            lastKnownAngle = 0;
            RotationGestureDetector.setAngle(0);
            timerThread = new Thread(() -> {
                try {
                    Thread.currentThread().sleep(timeUntilFade);
                    while (alpha.get() > 0) {
                        alpha.getAndAdd(-5);
                        Thread.currentThread().sleep(20);
                    }
                    alpha.set(0);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            });
            timerThread.setName("Compass");
            timerThread.start();
        }
        return false;
    }
}
