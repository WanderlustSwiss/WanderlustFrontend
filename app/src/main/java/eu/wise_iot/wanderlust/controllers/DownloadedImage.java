package eu.wise_iot.wanderlust.controllers;

import java.io.File;

public class DownloadedImage {
    private final File image;
    private final long size;
    private final boolean isPublic;

    DownloadedImage(File image, long size, boolean isPublic) {
        this.image = image;
        this.size = size;
        this.isPublic = isPublic;
    }

    public File getImage() {
        return image;
    }

    public long getSize() {
        return size;
    }

    public boolean isPublic() {
        return isPublic;
    }
}