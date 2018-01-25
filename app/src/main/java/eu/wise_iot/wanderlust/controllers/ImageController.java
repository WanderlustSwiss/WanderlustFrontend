package eu.wise_iot.wanderlust.controllers;


import android.content.Context;
import android.os.AsyncTask;

import org.greenrobot.essentials.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;

public class ImageController {

    public static String picturesDir;
    private static Context mainContext;

    public static void init(Context context){
        mainContext = context;
        picturesDir = mainContext.getApplicationContext().getFilesDir().getAbsolutePath() + "/pictures";

        File pictures = new File(picturesDir);
        if(!pictures.exists()) pictures.mkdir();

        File dir = new File(picturesDir + "/pois");
        dir.mkdir();
        dir = new File(picturesDir + "/tours");
        dir.mkdir();
        dir = new File(picturesDir + "/profile");
        dir.mkdir();
    }

    public static List<File> getImages(List<ImageInfo> imageInfos){
        List<File> images = new ArrayList<>();
        for(ImageInfo imageInfo : imageInfos){
            images.add(new File(picturesDir + "/" + imageInfo.getPath()));
        }
        return images;
    }

    public static void save(File file, ImageInfo image) throws IOException {

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

    public static boolean delete(ImageInfo imageInfo){
        File f = new File(imageInfo.getPath());
        return f.delete();
    }
}
