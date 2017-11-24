package eu.wise_iot.wanderlust.models.Old;

import android.location.Location;

/**
 * Tour:
 * @author Fabian Schwander
 * @license MIT
 */
public class Tour {
    private int tourId;
    private String editor;
    private String name;
    private int difficulty;
    private String duration;
    private int distanceUp;
    private int distanceDown;
    private String teaserImage;
    private String trackSegment; // todo: alist of track trackSegment
    private Location startLocation; // todo: can be retrieved from tracksegment.get(0)
    private Location endLocation; // todo: can be retrieved from tracksegment.get(n)
    private String dateTour;
    private String dateLastEdit;
    private String dateFirstEdit;
    private String description;
    private String linkSource;

    public Tour(int tourId, String editor, String name, int difficulty, String duration, int distanceUp, int distanceDown, String teaserImage, String trackSegment, Location startLocation, Location endLocation, String dateTour, String dateLastEdit, String dateFirstEdit, String description, String linkSource) {
        this.tourId = tourId;
        this.editor = editor;
        this.name = name;
        this.difficulty = difficulty;
        this.duration = duration;
        this.distanceUp = distanceUp;
        this.distanceDown = distanceDown;
        this.teaserImage = teaserImage;
        this.trackSegment = trackSegment;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.dateTour = dateTour;
        this.dateLastEdit = dateLastEdit;
        this.dateFirstEdit = dateFirstEdit;
        this.description = description;
        this.linkSource = linkSource;
    }

    public String getTeaserImageWithoutSuffix() {
        return teaserImage.substring(0, teaserImage.indexOf("."));
    }

    public String getDifficultyWithPrefix() {
        return "T" + difficulty;
    }

    public String getDifficultyWithExplainingText() {
        String text = "T" + difficulty + " ";
        switch (difficulty) {
            case 1:
                text += "(Wandern)";
                break;
            case 2:
                text += "(Bergwandern)";
                break;
            case 3:
                text += "(anspruchsvolles Bergwandern)";
                break;
            case 4:
                text += "(Alpinwandern)";
                break;
            case 5:
                text += "(anspruchsvolles Alpinwandern)";
                break;
            case 6:
                text += "(schwieriges Alpinwandern)";
                break;
            default:
                text += "(ungültiger Schwierigkeitsgrad)";
        }
        return text;
    }

    public String getDistanceUpInMeters() {
        return distanceUp + "m";
    }

    public String getDistanceDownInMeters() {
        return distanceDown + "m";
    }

    /*     ________________________________________________________________________________________
     *    |                                                                                        |
     *    |                                 GETTERS AND SETTERS                                    |
     *    |________________________________________________________________________________________|
     */

    public int getTourId() {
        return tourId;
    }

    public String getName() {
        return name;
    }

    public String getEditor() {
        return editor;
    }

    public String getDateTour() {
        return dateTour;
    }

    public String getDateLastEdit() {
        return dateLastEdit;
    }

    public String getDateFirstEdit() {
        return dateFirstEdit;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getDuration() {
        return duration;
    }

    public int getDistanceUp() {
        return distanceUp;
    }

    public int getDistanceDown() {
        return distanceDown;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public void setDateTour(String dateTour) {
        this.dateTour = dateTour;
    }

    public void setDateLastEdit(String dateLastEdit) {
        this.dateLastEdit = dateLastEdit;
    }

    public void setDateFirstEdit(String dateFirstEdit) {
        this.dateFirstEdit = dateFirstEdit;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setDistanceUp(int distanceUp) {
        this.distanceUp = distanceUp;
    }

    public void setDistanceDown(int distanceDown) {
        this.distanceDown = distanceDown;
    }

    public void setTeaserImage(String teaserImage) {
        this.teaserImage = teaserImage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeaserImage() {
        return teaserImage;
    }

    public String getLinkSource() {
        return linkSource;
    }

    public void setLinkSource(String linkSource) {
        this.linkSource = linkSource;
    }

    public String getTrackSegment() {
        return trackSegment;
    }

    public void setTrackSegment(String trackSegment) {
        this.trackSegment = trackSegment;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }
}
