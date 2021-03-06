package eu.wise_iot.wanderlust.controllers;

/**
 * FragmentHandler for fragments to handle backend requests
 * @author Tobias Rüegsegger
 * @license MIT
 */
@FunctionalInterface
public interface FragmentHandler<T> {
    void onResponse(ControllerEvent<T> controllerEvent);
}
