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
    long route_id;
    String title;
    String description;
    String imagePath;
    String polyline;
    long difficulty;
    long routeKit;
    boolean editable;

    public Route(long route_id, String title, String description, String imagePath, String polyline, long difficulty, long routeKit, boolean editable) {
        this.route_id = route_id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.polyline = polyline;
        this.difficulty = difficulty;
        this.routeKit = routeKit;
        this.editable = editable;
    }

    public long getRouteKit() {
        return routeKit;
    }

    public void setRouteKit(long routeKit) {
        this.routeKit = routeKit;
    }

    public long getRoute_id() {
        return route_id;
    }

    public void setRoute_id(long route_id) {
        this.route_id = route_id;
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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
