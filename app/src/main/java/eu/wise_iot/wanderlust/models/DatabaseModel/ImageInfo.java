package eu.wise_iot.wanderlust.models.DatabaseModel;

public class ImageInfo {
    public long id;
    public String path;
    public String name;

    public ImageInfo(){}

    public ImageInfo(long id) {
        this.id = id;
    }

    public ImageInfo(long id, String name, String dir){
        // Todo: Refactor id
        this.id = id;
        this.name = name;
        setPath(name, dir);
    }

    public void setPath(String name, String dir){
        this.path = dir + "/" + name;
    }

    public long getId() {
        return id;
    }
    public String getName(){ return name; }
    public String getPath() { return path;}
}
