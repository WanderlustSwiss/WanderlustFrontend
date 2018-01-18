package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.io.File;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * CommunityTours
 *
 * @author Alexander Weinbeck
 * @license MIT
 */

@Entity
public class UserTour extends AbstractModel {

    @Id
    long internal_id;
    long tour_id;
    String title;
    String description;
    String imagePath;
    String polyline;
    String elevation;
    long distance;
    long duration;
    long ascent;
    long descent;
    long difficulty;
    long tourKit;
    boolean editable;

    @Convert(converter = Poi.imageInfoConverter.class, dbType = String.class)
    List<Poi.ImageInfo> imagePaths;
    byte[] imageIds;
    int imageCount;

    public UserTour(long internal_id, long tour_id, String title, String description, String imagePath, String polyline, long difficulty, long tourKit, boolean editable) {
        this.internal_id = internal_id;
        this.tour_id = tour_id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.polyline = polyline;
        this.difficulty = difficulty;
        this.tourKit = tourKit;
        this.editable = editable;
    }

    public File getImageById(byte imageId) {
       // for (int i = 0; i < imageCount; i++) {
            //if (imageIds[i] == imageId) {
                String name = tour_id + "-" + 1 + ".jpg";
                return new File(DatabaseController.mainContext.getApplicationInfo().dataDir +
                        "/files/" + name);
       //     }
        //}
        //return null;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getTourKit() {
        return tourKit;
    }

    public void setTourKit(long tourKit) {
        this.tourKit = tourKit;
    }

    public long getTour_id() {
        return tour_id;
    }

    public void setTour_id(long tour_id) {
        this.tour_id = tour_id;
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

    public String getElevation() {
        return elevation;
    }

    public void setElevation(String elevation) {
        this.elevation = elevation;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getAscent() {
        return ascent;
    }

    public void setAscent(long ascent) {
        this.ascent = ascent;
    }

    public long getDescent() {
        return descent;
    }

    public void setDescent(long descent) {
        this.descent = descent;
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
