package eu.wise_iot.wanderlust.controllers;

/*
 * FragmentHandler for fragments to handle backend requests
 * @author Tobias Rüegsegger
 * @license MIT
 */
public interface FragmentHandler {
    void onResponse(ControllerEvent controllerEvent);
}
