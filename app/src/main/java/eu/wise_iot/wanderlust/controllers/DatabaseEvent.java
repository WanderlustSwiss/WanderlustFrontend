package eu.wise_iot.wanderlust.controllers;

/*
 * ControllerEvent has information of a made request
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class DatabaseEvent {

    public enum SyncType{
        POI, POITYPE;
    }

    private SyncType type;

    DatabaseEvent(SyncType type){
        this.type = type;
    }

    public SyncType getType(){
        return type;
    }
}
