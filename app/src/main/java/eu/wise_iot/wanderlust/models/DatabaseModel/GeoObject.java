package eu.wise_iot.wanderlust.models.DatabaseModel;



public class GeoObject {

    public GeoObject() {
    }

    public GeoObject(String title, String description, float latitude, float longitude, int elevation, String sourceLink, String sourceName, String imageLink) {
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.elevation = elevation;

        this.sourceLink = sourceLink;
        this.sourceName = sourceName;
        this.imageLink = imageLink;
    }

    private String title;
    private String description;
    private float latitude;
    private float longitude;
    private int elevation;
    private String sourceLink;
    private String sourceName;
    private String imageLink;

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

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getElevation() {
        return elevation;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

}
