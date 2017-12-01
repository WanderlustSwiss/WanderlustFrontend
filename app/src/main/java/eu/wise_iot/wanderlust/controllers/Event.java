package eu.wise_iot.wanderlust.controllers;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;

/*
 * Event has information of a made request
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class Event {

    private EventType type;
    private AbstractModel model;
    private List<AbstractModel> modelList;
    /**
     * Create response event
     * @param event
     * @param model
     */
    public Event(EventType event, AbstractModel model){
        this.type = event;
        this.model = model;
    }

    public Event(EventType event, List<AbstractModel> modelList){
        this.type = event;
        this.modelList = modelList;
    }

    public Event(EventType event){
        this.type = event;
    }

    public EventType getType(){ return  this.type; }
    public AbstractModel getModel() { return this.model; }
    public List<AbstractModel> getModelList() { return this.modelList; }



}
