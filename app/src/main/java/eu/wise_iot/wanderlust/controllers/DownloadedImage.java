package eu.wise_iot.wanderlust.controllers;

import java.io.File;

public class DownloadedImage{
    private File image;
    private long size;
    private boolean isPublic;

    DownloadedImage(File image, long size, boolean isPublic){
        this.image = image;
        this.size = size;
        this.isPublic = isPublic;
    }

    public File getImage() { return image; }

    public long getSize() { return size; }

    public boolean isPublic() { return isPublic; }
}