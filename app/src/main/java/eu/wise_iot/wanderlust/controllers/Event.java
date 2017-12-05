package eu.wise_iot.wanderlust.controllers;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;

/*
 * Event has information of a made request
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class Event<T> {

    private EventType type;
    private T model;
    /**
     * Create response event
     * @param event
     * @param model
     */
    public Event(EventType event, T model){
        this.type = event;
        this.model = model;
    }

    public Event(EventType event){
        this.type = event;
    }

    public EventType getType(){ return  this.type; }
    public T getModel() { return this.model; }



}
