package eu.wise_iot.wanderlust.controllers;

/**
 * ControllerEvent has information of a made request
 * Used mainly by a functional interface see FragmentHandler
 *
 * @author Tobias Rüegsegger
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */
public class ControllerEvent<T> {

    private final EventType type;
    private T model;

    private int percentage;
    private String message;


    public ControllerEvent(EventType type, int percentage, String message) {
        this.type = type;
        this.percentage = percentage;
        this.message = message;
    }

    public ControllerEvent(EventType type, String message) {
        this.type = type;
        this.message = message;
    }

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
        type = event;
    }

    public EventType getType() {
        return type;
    }

    public T getModel() {
        return model;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
