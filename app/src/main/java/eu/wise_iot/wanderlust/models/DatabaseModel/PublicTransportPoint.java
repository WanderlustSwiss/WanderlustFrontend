package eu.wise_iot.wanderlust.models.DatabaseModel;

import org.osmdroid.util.GeoPoint;

/**
 * PublicTransportPoint
 *
 * PublicTransportPoint with Coordinates and and description
 */
public class PublicTransportPoint {
    private GeoPoint geoPoint;
    private String title;

    private int id;

    public PublicTransportPoint(GeoPoint geoPoint, String title, int id) {
        this.geoPoint = geoPoint;
        this.title = title;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public PublicTransportPoint() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




}
