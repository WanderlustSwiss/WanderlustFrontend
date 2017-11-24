package eu.wise_iot.wanderlust.models.Old;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Feedback:
 * @author Fabian Schwander
 * @license MIT
 */
public class Feedback {
    private long id; // todo: get data from db
    private String timestamp;
    private int feedbackType;
    private int displayMode;
    private double lat;
    private double lon;
    private String imageName;
    private String description;

    public Feedback(int displayMode, int feedbackType, double lat, double lon, String imageName, String description) {
        this.displayMode = displayMode;
        this.feedbackType = feedbackType;
        this.lat = lat;
        this.lon = lon;
        this.imageName = imageName;
        this.description = description;

        timestamp = getDateAsString();
        id = new Random().nextInt(300000000); // todo: delete after getting data from db
        while (id < 100) {
            id = new Random().nextInt(300000000);
        }
    }

    private String getDateAsString() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return simpleDateFormat.format(new Date());
    }

    public String getImageNameWithoutSuffix() {
        String noSuffixName = imageName;
        if (imageName != null) {
            noSuffixName = imageName.replace(".jpg", "");
        }
        return noSuffixName;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getImagename() {
        return imageName;
    }

    public void setImagename(String image_name) {
        this.imageName = image_name;
    }

    public int getFeedbackType() {
        return feedbackType;
    }

    public void setFeedbackType(int feedbackType) {
        this.feedbackType = feedbackType;
    }

    public int getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
