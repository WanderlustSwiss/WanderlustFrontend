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
     * @param type represents the type as HTTP code
     * @param model represents the object of the event.
     *              Could also be a list objects
     */
    public Event(EventType type, T model){
        this.type = type;
        this.model = model;
    }

    public Event(EventType event){
        this.type = event;
    }

    public EventType getType(){ return  this.type; }
    public T getModel() { return this.model; }



}
