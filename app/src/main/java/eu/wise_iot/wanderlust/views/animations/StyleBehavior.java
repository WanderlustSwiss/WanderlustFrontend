package eu.wise_iot.wanderlust.views.animations;

import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.View;

/**
 * Adds style behaviour to controls
 *
 * @author Fabian Schwander
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class StyleBehavior {
    public static void buttonEffectOnTouched(final View button) {
        button.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    // old color: not in style guideline but looked pretty nice
//                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                    // todo: add color from resource file here dynamically and not as fix color
                    v.getBackground().setColorFilter(0xe0F2BF30, PorterDuff.Mode.SRC_ATOP);
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
        });
    }
}
