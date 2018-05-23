package eu.wise_iot.wanderlust.models.DatabaseModel;

public class ImageInfo {
    private long id;
    private String path;
    private volatile String localDir;
    private String name;

    public ImageInfo(){}

    public ImageInfo(long id) {
        this.id = id;
    }

    public ImageInfo(long id, String name, String localDir){
        this.id = id;
        this.name = name;
        this.localDir = localDir;
    }
    public void setPath(String path){ this.path = path; }
    public void setLocalDir(String localDir) {this.localDir = localDir;}
    //public void setId(long id){ this.id = id; }
    public void setName(String name){ this.name = name; }

    public String getLocalDir(){ return localDir; }
    public String getLocalPath(){ return localDir + '/' + name; }
    public long getId() {
        return id;
    }
    public String getName(){ return name; }
    public String getPath() { return path;}
}
