package eu.wise_iot.wanderlust.models.DatabaseModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;

/**
 * Poi
 * @author Rilind Gashi
 * @author Tobias Rüegsegger
 * @license MIT
 */

@Entity
public class Poi extends AbstractModel{

    @Id
    long poi_id;
    String title;
    String description;

    @Convert(converter =  imageInfoConverter.class, dbType = String.class)
    List<ImageInfo> imagePaths;
    double longitude;
    double latitude;
    long user;
    long type;
    boolean isPublic;

    public Poi(long poi_id, String name, String description, List<ImageInfo> picturePath,
               double longitude, double latitude, long user, long type, boolean isPublic) {
        this.poi_id = poi_id;
        this.title = name;
        this.description = description;
        this.imagePaths = picturePath;
        this.longitude = longitude;
        this.latitude = latitude;
        this.user = user;
        this.type = type;
        this.isPublic = isPublic;
    }

    public List<ImageInfo> getImagePath() { return imagePaths; }

    public void setImagePath(List<ImageInfo> imagePath) { this.imagePaths = imagePath; }

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

    //@Entity
    public class ImageInfo{
        //@Id
        long id;
        String name;
        String path;

        public ImageInfo(int id, String name, String path) {
            this.id = id;
            this.name = name;
            this.path = path;
        }

        public long getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class imageInfoConverter implements PropertyConverter<List<ImageInfo>, String> {
        @Override
        public List<ImageInfo> convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            Gson gson = new Gson();
            return gson.fromJson(databaseValue, new TypeToken<List<ImageInfo>>() {}.getType());
        }

        @Override
        public String convertToDatabaseValue(List<ImageInfo> entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            Gson gson = new Gson();
            return gson.toJson(entityProperty, new TypeToken<List<ImageInfo>>() {}.getType());
        }
    }

}
