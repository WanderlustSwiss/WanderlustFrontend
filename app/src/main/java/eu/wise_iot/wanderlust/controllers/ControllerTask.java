package eu.wise_iot.wanderlust.controllers;

/**
 * ControllerNotification has information of a made requests progress
 *
 * @author Alexander Weinbeck
 * @license MIT
 */
public class ControllerTask {

    private int percentage;
    private String message;


    public ControllerTask(int percentage, String message) {
        this.percentage = percentage;
        this.message = message;
    }
    public ControllerTask(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }

    public String getMessage() { return message; }

}
