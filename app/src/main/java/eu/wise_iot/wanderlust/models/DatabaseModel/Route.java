package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Route
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Route extends AbstractModel{

    @Id
    long routeId;
    String title;
    String description;
    String picturePath;
    String polyline;
    long difficulty;
    int weatherRegion;
    boolean editable;

    public Route(long routeId, String title, String description, String picturePath,
                 String polyline, long difficulty, int weatherRegion, boolean editable) {
        this.routeId = routeId;
        this.title = title;
        this.description = description;
        this.picturePath = picturePath;
        this.polyline = polyline;
        this.difficulty = difficulty;
        this.weatherRegion = weatherRegion;
        this.editable = editable;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getPolyline() {
        return polyline;
    }

    public void setPolyline(String polyline) {
        this.polyline = polyline;
    }

    public long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

    public int getWeatherRegion() {
        return weatherRegion;
    }

    public void setWeatherRegion(int weatherRegion) {
        this.weatherRegion = weatherRegion;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
