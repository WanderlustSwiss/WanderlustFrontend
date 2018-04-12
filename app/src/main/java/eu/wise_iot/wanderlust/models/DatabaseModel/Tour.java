package eu.wise_iot.wanderlust.models.DatabaseModel;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.PolyLineEncoder;
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
public class Tour extends AbstractModel {

    @Id
    long internal_id;
    long tour_id;
    String title;
    String description;
    String imagePath;
    String polyline;
    String elevation;
    String createdAt;
    String updatedAt;
    long distance;
    long duration;
    long ascent;
    long descent;

    long region;
    long difficulty;
    long tourKit;
    boolean editable;

    @Convert(converter = Poi.imageInfoConverter.class, dbType = String.class)
    List<ImageInfo> imagePaths;

    public Tour(long internal_id, long tour_id, String title, String description,
                    String imagePath, String polyline, String elevation, long duration, long distance,
                    long ascent, long descent, long difficulty, long tourKit, boolean editable,
                    String updatedAt, String createdAt, long region) {
        this.internal_id = internal_id;
        this.tour_id = tour_id;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.polyline = polyline;
        this.elevation = elevation;
        this.duration = duration;
        this.distance = distance;
        this.ascent = ascent;
        this.descent = descent;
        this.difficulty = difficulty;
        this.tourKit = tourKit;
        this.editable = editable;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.region = region;
    }

    public Tour(){
        this.internal_id = 0;
        this.title = "No title";
        this.description = "No description";
    }

    public List<GeoPoint> getGeoPoints(){
        return PolyLineEncoder.decode(this.getPolyline(), 10);
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

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public long getRegion() {
        return region;
    }

    public void setRegion(long region) {
        this.region = region;
    }
}
