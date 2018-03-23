package eu.wise_iot.wanderlust.controllers;

import android.view.MotionEvent;

public interface MotionEventListener<E> {
    void update(E arg, MotionEvent event);

}
