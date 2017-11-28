package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;

/*
 * Event has information of a made request
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class Event {

    private EventType type;
    private AbstractModel model;

    /**
     * Create response event
     * @param event
     * @param model
     */
    public Event(EventType event, AbstractModel model){
        this.type = event;
        this.model = model;
    }

    public EventType getType(){ return  this.type; }
    public AbstractModel getModel() { return this.model; }
}
