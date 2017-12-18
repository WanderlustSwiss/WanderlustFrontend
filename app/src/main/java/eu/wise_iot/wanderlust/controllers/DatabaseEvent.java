package eu.wise_iot.wanderlust.controllers;

/*
 * ControllerEvent has information of a made request
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class DatabaseEvent<T> {

    private SyncType type;
    private T obj;

    public DatabaseEvent(SyncType type) {
        this.type = type;
    }

    public DatabaseEvent(SyncType type, T obj) {
        this.type = type;
        this.obj = obj;
    }

    public SyncType getType() {
        return type;
    }

    public T getObj() {
        return obj;
    }

    public enum SyncType {
        POI, SINGLEPOI, POITYPE, POIAREA, DELETESINGLEPOI, EDITSINGLEPOI;
    }
}
