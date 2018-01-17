package eu.wise_iot.wanderlust.models.DatabaseModel;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class MapSearchResult {
    private String locality;
    private double latitude;
    private double longitude;
    private ArrayList<ArrayList<GeoPoint>> polygons;

    public MapSearchResult() {
        polygons = new ArrayList<>();
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public void setLatitued(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setPolygon(ArrayList<GeoPoint> geopoints) {
        this.polygons.add(geopoints);
    }

    public String getLocality() {
        return this.locality;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public ArrayList<ArrayList<GeoPoint>>  getPolygon() {
        return this.polygons;
    }
}
