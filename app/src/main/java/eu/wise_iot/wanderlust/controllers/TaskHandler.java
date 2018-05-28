package eu.wise_iot.wanderlust.controllers;

/**
 * FragmentHandler for fragments to handle backend requests
 *
 * @author Alexander Weinbeck
 * @license MIT
 */

@FunctionalInterface
public interface TaskHandler<T> {
    void onNotify(ControllerTask controllerNotification);
}
