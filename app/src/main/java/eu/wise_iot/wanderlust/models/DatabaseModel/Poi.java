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
    long id;
    String name;
    String description;
    String picturePath;
    double longitude;
    double latitude;
    long userId;

    public Poi(long id, String name, String description, String picturePath, double longitude, double latitude, long userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picturePath = picturePath;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
