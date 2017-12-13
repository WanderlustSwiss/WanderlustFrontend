package eu.wise_iot.wanderlust.models.DatabaseModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;

/**
 * Profile
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Profile extends AbstractModel{

    @Id
    long internal_id;
    @Convert(converter =  imageInfoConverter.class, dbType = String.class)
    ImageInfo imagePath;

    long profile_id;
    byte imageId;
    int score;
    String birthday;
    String language;
    long user;
    long difficulty;

    public Profile(long internal_id, long profile_id, byte imageId, int score, String birthday, String language, long user, long difficulty) {
        this.internal_id = internal_id;
        this.profile_id = profile_id;
        this.imageId = imageId;
        this.score = score;
        this.birthday = birthday;
        this.language = language;
        this.user = user;
        this.difficulty = difficulty;
    }

    public long getInternal_id() {
        return profile_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public byte getImageId() { return imageId; }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    public long getUser() { return user; }

    public void setUser(long user) { this.user = user; }

    public long getDifficulty() { return difficulty; }

    public void setDifficulty(long difficulty) { this.difficulty = difficulty; }

    public long getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(long profile_id) {
        this.profile_id = profile_id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean removeImageId(byte id){
        if(imageId == id)
        {
            imageId = 0;
            return true;
        }
        return false;
    }

    public File getImageById(byte imageId){
        if(imageId == imageId){
            String name = profile_id + "-" + imageId + ".jpg";
            return new File(DatabaseController.mainContext.getApplicationInfo().dataDir +
                    "/files/" + name);
        }
        return null;
    }

    public void setImageId(byte imageId){
        this.imageId = imageId;
    }

    public ImageInfo getImagePath(){
        return imagePath;
    }

    public class ImageInfo{
        long id;
        String name;
        String path;

        public ImageInfo(long id, String name, String path) {
            this.id = id;
            this.name = name;
            this.path = path;
        }

        public long getId() {
            return id;
        }
    }

    public static class imageInfoConverter implements PropertyConverter<ImageInfo, String> {
        @Override
        public ImageInfo convertToEntityProperty(String databaseValue) {
            if (databaseValue == null) {
                return null;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<ImageInfo>() {
            }.getType();
            return gson.fromJson(databaseValue, type);
        }

        @Override
        public String convertToDatabaseValue(ImageInfo entityProperty) {
            if (entityProperty == null) {
                return null;
            }
            Gson gson = new Gson();
            Type type = new TypeToken<ImageInfo>() {
            }.getType();
            return gson.toJson(entityProperty, type);
        }
    }


}

