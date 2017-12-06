package eu.wise_iot.wanderlust.models.DatabaseModel;

import android.os.Environment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
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
    long internalId;
    long poi_id;
    String title;
    String description;
    String createdAt;

    @Convert(converter =  imageInfoConverter.class, dbType = String.class)
    List<ImageInfo> imagePaths;
    float longitude;
    float latitude;
    int rate;
    long user;
    long type;
    boolean isPublic;

    public Poi(long poi_id, String name, String description, List<ImageInfo> picturePath,
               float longitude, float latitude, int rate, long user, int type, boolean isPublic) {
        this.poi_id = poi_id;
        this.title = name;
        this.description = description;
        this.imagePaths = picturePath;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rate = rate;
        this.user = user;
        this.type = type;
        this.isPublic = isPublic;
    }

    public Poi(){
        this.internalId = 0;
        this.title = "No Title";
        this.description = "No Description";
        this.imagePaths = new ArrayList<>();
        this.longitude = 0;
        this.latitude = 0;
        this.rate = 3;
        this.user = 1;
        this.type = 0;
        this.isPublic = false;
    }

    public ImageInfo createImageInfo(int id, String name, String path){
        return new ImageInfo(id, name, path);
    }

    public List<ImageInfo> getImagePath() { return imagePaths; }

    public void addImageInfo(long id, String name, String path){
        this.imagePaths.add(new ImageInfo(id, name, path));
    }

    public String getCreatedAt() {
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterString = new SimpleDateFormat("yyyy MMMM dd", Locale.GERMAN);
        try{
            Date date = formatterDate.parse(createdAt.substring(0, createdAt.indexOf('T')));
            return formatterString.format(date);
        } catch(Exception e){
            return "invalid Date";
        }
    }

    public String getCreatedAtInGerman() {
        SimpleDateFormat formatterDate = new SimpleDateFormat("dd-MM-yy");
        SimpleDateFormat formatterString = new SimpleDateFormat("d. MMMM yyyy", Locale.GERMAN);
        try{
            Date date = formatterDate.parse(createdAt.substring(0, createdAt.indexOf('T')));
            return formatterString.format(date);
        } catch(Exception e){
            return "invalid Date";
        }
    }

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

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
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

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public class ImageInfo extends AbstractModel{
        long id;
        String name;
        String path;

        public ImageInfo(long id, String name, String path) {
            this.id = id;
            this.name = name;
            this.path = path;
        }

        public String getName() { return name; }

        public long getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPath(){
            return this.path;
        }

        public File getImage(boolean isPublic){
            File image;
            if(isPublic) {
                image = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        this.name);
            } else{
                image = new File(DatabaseController.mainContext.getApplicationInfo().dataDir
                        + this.name);
            }
            return image;
        }

    }

    public static class imageInfoConverter implements PropertyConverter<List<ImageInfo>, String> {
        @Override
        public List<ImageInfo> convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            Gson gson = new Gson();
            List<ImageInfo> list = gson.fromJson(databaseValue, new TypeToken<List<ImageInfo>>() {}.getType());
            return list;
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
