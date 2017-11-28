package eu.wise_iot.wanderlust.controllers;

/**
 * Created by Joshi on 28.11.2017.
 */

public class Event {
    public enum EventType{
        SUCCESSFUL, NOT_FOUND, BAD_REQUEST, SERVER_ERROR
    }

    private EventType type;

    public Event(EventType event){
        this.type = event;
    }

    public EventType getType(){
        return  this.type;
    }
}
