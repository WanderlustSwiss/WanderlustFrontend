package eu.wise_iot.wanderlust.controllers;

/**
 * FragmentHandler for fragments to handle backend requests
 * @author Tobias Rüegsegger
 * @license GPL-3.0
 */
@FunctionalInterface
public interface FragmentHandler<T> {
    void onResponse(ControllerEvent<T> controllerEvent);
}

