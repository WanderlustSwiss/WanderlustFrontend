package eu.wise_iot.wanderlust.controllers;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

/*
 * ControllerEvent has information of a made request
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class DatabaseEvent<T> {

    public enum SyncType{
        POI, SINGLEPOI, POITYPE, POIAREA, DELETESINGLEPOI, EDITSINGLEPOI;
    }

    private SyncType type;
    private T obj;

    public DatabaseEvent(SyncType type){
        this.type = type;
    }

    public DatabaseEvent(SyncType type, T obj){
        this.type = type;
        this.obj = obj;
    }

    public SyncType getType(){
        return type;
    }

    public T getObj(){
        return obj;
    }
}
