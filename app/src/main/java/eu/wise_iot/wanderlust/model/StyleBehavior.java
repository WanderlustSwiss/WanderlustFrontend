package eu.wise_iot.wanderlust.model;

import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

/**
 * StyleBehavior:
 * @author Fabian Schwander
 * @author Alexander Weinbeck
 * @license MIT
 */
public class StyleBehavior {
    public static void  buttonEffectOnTouched(final View button) {
        button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        v.getBackground().clearColorFilter();
                        v.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
    }
}
