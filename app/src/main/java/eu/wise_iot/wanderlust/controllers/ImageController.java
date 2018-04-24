package eu.wise_iot.wanderlust.controllers;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ImageController {
    private static String TAG = "ImageController";
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

    private final String picturesDir;
    private final String[] FOLDERS;
    private final int standardWidth;

    public ImageController(){
        picturesDir = CONTEXT.getApplicationContext().getApplicationContext().getExternalFilesDir("pictures").getAbsolutePath();
        FOLDERS = new String[4];
        FOLDERS[0] = "poi";
        FOLDERS[1] = "tours";
        FOLDERS[2] = "profile";
        FOLDERS[3] = "equipment";
        standardWidth = 1024;

        File pictures = new File(picturesDir);
        if(!pictures.exists()) pictures.mkdir();
        for (int i = 0; i < FOLDERS.length; i++){
            File dir = new File(picturesDir + "/" + FOLDERS[i]);
            dir.mkdir();
        }
    }
    public String getPoiFolder(){
        return FOLDERS[0];
    }
    public String getTourFolder(){
        return FOLDERS[1];
    }
    public String getProfileFolder(){
        return FOLDERS[2];
    }
    public String getEquipmentFolder() { return FOLDERS [3]; }

    public List<File> getImages(List<ImageInfo> imageInfos){
        List<File> images = new ArrayList<>();
        for(ImageInfo imageInfo : imageInfos){
            images.add(new File(picturesDir + "/" + imageInfo.getLocalPath()));
        }
        return images;
    }
    public File getImage(ImageInfo imageInfo){
        if (imageInfo == null){
            return null;
        }
        return new File(picturesDir + "/" + imageInfo.getLocalPath());
    }
    public void save(File file, ImageInfo image) throws IOException {

        InputStream in = new FileInputStream(file);
        FileOutputStream out = new FileOutputStream(picturesDir + "/" + image.getLocalPath());


        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    public void save(InputStream in, ImageInfo image) throws IOException {

        FileOutputStream out = new FileOutputStream(picturesDir + "/" + image.getLocalPath());

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
    }

    public boolean delete(ImageInfo imageInfo){
        File f = new File(imageInfo.getLocalPath());
        return f.delete();
    }

    public String getPicturesDir() {
        return picturesDir;
    }

    public File resize(File imgFileOrig) throws IOException {
        return resize(imgFileOrig, standardWidth);
    }

    public File resize(File imgFileOrig, int destWidth) throws IOException {
        Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath());
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();

        if(origWidth > destWidth){
            int destHeight = origHeight/( origWidth / destWidth ) ;
            Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            b2.compress(Bitmap.CompressFormat.JPEG,80 , outStream);
            File f = new File(imgFileOrig.getAbsolutePath());
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(outStream.toByteArray());
            fo.close();
            return f;
        }
        return imgFileOrig;
    }

    public Picasso getPicassoHandler(Activity context){
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Cookie", LoginUser.getCookies().get(0))
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        Picasso picasso = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();

        return picasso;
    }
}
