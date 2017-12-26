package eu.wise_iot.wanderlust.models.DatabaseModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
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

    private static final int MAX_IMAGES = 32;

    @Id
    long internal_id;
    long poi_id;
    String title;
    String description;
    String createdAt;

    @Convert(converter = imageInfoConverter.class, dbType = String.class)
    List<ImageInfo> imagePaths;
    byte[] imageIds;
    int imageCount;
    float longitude;
    float latitude;
    int rate;
    long user;
    long type;
    boolean isPublic;

    public Poi(long poi_id, String name, String description, byte[] imageIds, float longitude,
               float latitude, int rate, long user, int type, boolean isPublic, int imageCount) {
        this.poi_id = poi_id;
        this.title = name;
        this.description = description;
        this.imageIds = imageIds;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rate = rate;
        this.user = user;
        this.type = type;
        this.isPublic = isPublic;
        this.imageCount = imageCount;
    }

    public Poi() {
        this.internal_id = 0;
        this.title = "No Title";
        this.description = "No Description";
        this.imageIds = new byte[MAX_IMAGES];
        this.imageCount = 0;
        this.longitude = 0;
        this.latitude = 0;
        this.rate = 3;
        this.user = 1;
        this.type = 0;
        this.isPublic = false;
    }

    public byte[] getImageIds() {
        return imageIds;
    }

    public void addImageId(byte id) {
        imageIds[imageCount++] = id;
    }

    public boolean removeImageId(byte id) {
        int index = 0;
        while (imageIds[index] != id) {
            index++;
            if (index == imageCount) {
                return false;
            }
        }
        for (int i = index; i < imageCount; i++) {
            imageIds[i] = imageIds[i + 1];
        }
        imageCount--;
        return true;
    }

    public File getImageById(byte imageId) {
        for (int i = 0; i < imageCount; i++) {
            if (imageIds[i] == imageId) {
                String name = poi_id + "-" + imageIds[i] + ".jpg";
                return new File(DatabaseController.picturesDir + "/" + name);
            }
        }
        return null;
    }

    public void setImageIds(byte[] imageIds, int imageCount) {
        this.imageIds = imageIds;
        this.imageCount = imageCount;
    }

    public int getImageCount() {
        return imageCount;
    }

    public List<ImageInfo> getImagePaths() {
        return imagePaths;
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

    public class ImageInfo {
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

        public String getPath() {
            return this.path;
        }
    }
}
