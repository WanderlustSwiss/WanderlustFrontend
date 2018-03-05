package eu.wise_iot.wanderlust.models.DatabaseModel;

public class ImageInfo {
    public long id;
    public String path;

    public ImageInfo(){}

    public ImageInfo(long id) {
        this.id = id;
    }

    public ImageInfo(long id, String name, String dir){
        // Todo: Refactor id
        this.id = id;
        setPath(name, dir);
    }

    public void setPath(String name, String dir){
        this.path = dir + "/" + name;
    }

    public long getId() {
        return id;
    }
    public String getPath() { return path;}
}
