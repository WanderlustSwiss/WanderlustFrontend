package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;

/**
 * Created by Joshi on 28.11.2017.
 */

public class Event {
    public enum EventType{
        SUCCESSFUL, NOT_FOUND, BAD_REQUEST, SERVER_ERROR
    }

    private EventType type;
    private AbstractModel model;

    public Event(EventType event, AbstractModel model){
        this.type = event;
        this.model = model;
    }

    public EventType getType(){
        return  this.type;
    }
    public AbstractModel getModel() { return this.model; }
}
