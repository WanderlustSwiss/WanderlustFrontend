package eu.wise_iot.wanderlust.controllers;

/**
 * ControllerEvent has information of a made request
 *
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class ControllerEvent<T> {


    private final EventType type;
    private T model;

    /**
     * Create response event
     *
     * @param type  represents the type as HTTP code
     * @param model represents the object of the event.
     *              Could also be a list objects
     */
    public ControllerEvent(EventType type, T model) {
        this.type = type;
        this.model = model;
    }

    public ControllerEvent(EventType event) {
        this.type = event;
    }

    public EventType getType() {
        return this.type;
    }

    public T getModel() {
        return this.model;
    }


}
