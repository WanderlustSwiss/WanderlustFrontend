package eu.wise_iot.wanderlust.models.DatabaseModel;

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
    String imagePath;
    String polyline;
    long difficulty;
    long tourKit;
    boolean editable;

    public CommunityTour(long internal_id, long tour_id, String title, String description, String imagePath, String polyline, long difficulty, long tourKit, boolean editable) {
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
