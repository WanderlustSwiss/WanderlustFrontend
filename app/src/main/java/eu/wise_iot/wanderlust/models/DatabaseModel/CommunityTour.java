package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.io.File;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * CommunityTour
 *
 * @author Alexander Weinbeck, Rilind Gashi
 * @license MIT
 */

@Entity
public class CommunityTour extends AbstractModel {

    @Id
    long internal_id;
    long userTour_id;
    long userTourModel;
    long tour_id;
    String title;
    String description;

    @Convert(converter = Poi.imageInfoConverter.class, dbType = String.class)
    List<Poi.ImageInfo> imagePaths;
    byte[] imageIds;
    int imageCount;

    String polyline;
    long difficulty;
    long tourKit;
    boolean editable;

    public CommunityTour(long internal_id, long tour_id, String title, String description, byte[] imageIds, String polyline, long difficulty, long tourKit, boolean editable) {
        this.internal_id = internal_id;
        this.tour_id = tour_id;
        this.title = title;
        this.description = description;
        this.imageIds = imageIds;
        this.polyline = polyline;
        this.difficulty = difficulty;
        this.tourKit = tourKit;
        this.editable = editable;
    }

    public CommunityTour() {
        this.internal_id = 0;
        this.title = "No title";
        this.description = "No description";
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
                String name = tour_id + "-" + imageIds[i] + ".jpg";
                return new File(DatabaseController.mainContext.getApplicationInfo().dataDir +
                        "/files/" + name);
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

    public List<Poi.ImageInfo> getImagePaths() {
        return imagePaths;
    }
    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getTour_id() {
        return tour_id;
    }

    public void setUserTour_id(long userTour_id) {
        this.tour_id = userTour_id;
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

    public long getTourKit() {
        return tourKit;
    }

    public void setTourKit(long tourKit) {
        this.tourKit = tourKit;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
}
