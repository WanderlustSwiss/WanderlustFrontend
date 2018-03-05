package eu.wise_iot.wanderlust.controllers;


import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;

public class ImageController {
    private static class Holder {
        private static final ImageController INSTANCE = new ImageController();
    }

    private static Context CONTEXT;

    public static ImageController createInstance(Context context){
        CONTEXT = context;
        return Holder.INSTANCE;
    }

    public static ImageController getInstance(){
        return CONTEXT != null ? Holder.INSTANCE : null;
    }

    private String picturesDir;

    private ImageController(){
        picturesDir = CONTEXT.getApplicationContext().getApplicationContext().getExternalFilesDir("pictures").getAbsolutePath();
        String[] folders = {"pois", "tours", "profile"};

        File pictures = new File(picturesDir);
        if(!pictures.exists()) pictures.mkdir();
        for (int i = 0; i < folders.length; i++){
            File dir = new File(picturesDir + "/" + folders[i]);
            dir.mkdir();
        }
    }

    public List<File> getImages(List<ImageInfo> imageInfos){
        List<File> images = new ArrayList<>();
        for(ImageInfo imageInfo : imageInfos){
            images.add(new File(picturesDir + "/" + imageInfo.getPath()));
        }
        return images;
    }
    public File getImage(ImageInfo imageInfo){
        return new File(picturesDir + "/" + imageInfo.getPath());
    }

    public void save(File file, ImageInfo image) throws IOException {

        InputStream in = new FileInputStream(file);

        FileOutputStream out = new FileOutputStream(picturesDir + "/" + image.getPath());


        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    public boolean delete(ImageInfo imageInfo){
        File f = new File(imageInfo.getPath());
        return f.delete();
    }

    public String getPicturesDir() {
        return picturesDir;
    }
}
