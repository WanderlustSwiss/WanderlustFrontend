package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Poi
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Poi extends AbstractModel{

    @Id
    long poi_id;
    String title;
    String description;
    String imagePath;
    double longitude;
    double latitude;
    long user;
    long type;
    boolean isPublic;

    public Poi(long poi_id, String name, String description, String picturePath,
               double longitude, double latitude, long user, long type, boolean isPublic) {
        this.poi_id = poi_id;
        this.title = name;
        this.description = description;
        this.imagePath = picturePath;
        this.longitude = longitude;
        this.latitude = latitude;
        this.user = user;
        this.type = type;
        this.isPublic = isPublic;
    }

    public String getImagePath() { return imagePath; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public long getType() { return type; }

    public void setType(long type) { this.type = type; }

    public boolean isPublic() { return isPublic; }

    public void setPublic(boolean isPublic) { this.isPublic = isPublic; }

    public long getPoi_id() {
        return poi_id;
    }

    public void setPoi_id(long poi_id) {
        this.poi_id = poi_id;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }
}
