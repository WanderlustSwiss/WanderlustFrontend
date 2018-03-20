package eu.wise_iot.wanderlust.models.DatabaseModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.converter.PropertyConverter;

/**
 * Profile
 *
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Profile extends AbstractModel {

    @Id
    long internal_id;
    @Convert(converter = imageInfoConverter.class, dbType = String.class)
    ImageInfo imagePath;

    long profile_id;
    int score;
    int sex;
    String birthday;
    String language;
    long user;
    long difficulty;

    public Profile(long internal_id, long profile_id, int score, int sex, String birthday, String language, long user, long difficulty) {
        this.internal_id = internal_id;
        this.profile_id = profile_id;
        this.score = score;
        this.sex = sex;
        this.birthday = birthday;
        this.language = language;
        this.user = user;
        this.difficulty = difficulty;
    }

    public Profile() {
        this.internal_id = 0;
        this.profile_id = 1;
        this.score = 0;
        this.birthday = "01.01.1990";
        this.language = "Deutsch";
        this.user = 1;
        this.difficulty = 1;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(long difficulty) {
        this.difficulty = difficulty;
    }

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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public ImageInfo getImagePath() {
        return imagePath;
    }

    public void setImagePath(ImageInfo imagePath) {
        this.imagePath = imagePath;
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

