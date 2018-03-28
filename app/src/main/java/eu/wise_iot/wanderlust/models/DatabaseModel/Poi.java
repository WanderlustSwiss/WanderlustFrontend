package eu.wise_iot.wanderlust.models.DatabaseModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
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
 *
 * @author Rilind Gashi
 * @author Tobias Rüegsegger
 * @license MIT
 */

@Entity
public class Poi extends AbstractModel {

    @Id
    long internal_id;
    long poi_id;
    String title;
    String description;
    String createdAt;
    String updatedAt;

    @Convert(converter = imageInfoConverter.class, dbType = String.class)
    List<ImageInfo> imagePaths;

    float longitude;
    float latitude;
    float elevation;
    int rate;
    long user;
    long type;
    boolean isPublic;

    public Poi(long poi_id, String name, String description, float longitude,
               float latitude, float elevation, int rate, long user, int type, boolean isPublic,
               List<ImageInfo> imagePaths, String updatedAt, String createdAt) {
        this.poi_id = poi_id;
        this.title = name;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.elevation = elevation;
        this.rate = rate;
        this.user = user;
        this.type = type;
        this.isPublic = isPublic;
        this.imagePaths = imagePaths;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }
// Response{protocol=http/1.1, code=200, message=OK, url=http://10.0.2.2:1337/hashtag/poi?lat1=47.149700898999264&long1=7.857840401785716&lat2=46.840870347941916&long2=8.140345982142865&hash_id=2}
    public Poi() {
        this.internal_id = 0;
        this.title = "No title";
        this.description = "No description";
        this.longitude = 0;
        this.latitude = 0;
        this.elevation = 0;
        this.rate = 3;
        this.user = 1;
        this.type = 0;
        this.isPublic = false;
        this.imagePaths = new ArrayList<>();
    }

    public int getImageCount() {
        return imagePaths.size();
    }

    public void setImagePaths(List<ImageInfo> imagePaths){
        this.imagePaths = imagePaths;
    }

    public List<ImageInfo> getImagePaths() {
        return imagePaths;
    }

    public ImageInfo getImageById(long id){
        for(ImageInfo imageInfo : imagePaths){
            if(imageInfo.getId() == id){
                return imageInfo;
            }
        }
        return null;
    }

    public void addImagePath(ImageInfo imageInfo){
        imagePaths.add(imageInfo);
    }

    public boolean removeImage(ImageInfo imageInfo){

        for(ImageInfo info : imagePaths){
            if(info.getId() == imageInfo.getId()){
                imagePaths.remove(info);
                return true;
            }
        }
        return false;
    }

    public String getCreatedAt(Locale language) {
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterString = new SimpleDateFormat("d. MMMM yyyy", language);
        try {
            Date date = formatterDate.parse(createdAt.substring(0, createdAt.indexOf('T')));
            return formatterString.format(date);
        } catch (Exception e) {
            return "invalid Date";
        }
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public long getPoi_id() {
        return poi_id;
    }

    public void setPoi_id(long poi_id) {
        this.poi_id = poi_id;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
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

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
    public static class imageInfoConverter implements PropertyConverter<List<ImageInfo>, String> {
        @Override
        public List<ImageInfo> convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<ImageInfo>>() {
            }.getType();
            return gson.fromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(List<ImageInfo> entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<ImageInfo>>() {
            }.getType();
            return gson.toJson(entityProperty, type);
        }
    }
}
