package eu.wise_iot.wanderlust.controllers;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class ImageController {
    private static String TAG = "ImageController";
    private static class Holder {
        private static final ImageController INSTANCE = new ImageController();
    }

    private static Context CONTEXT;

    public static void createInstance(Context context){
        CONTEXT = context;
    }

    public static ImageController getInstance(){
        return CONTEXT != null ? Holder.INSTANCE : null;
    }

    private final String picturesDir;
    private final String[] FOLDERS;
    private final int standardWidth;
    private final Uri defaultImageURL;

    public final Cache cache;

    public ImageController(){
        defaultImageURL = Uri.parse("android.resource://eu.wise_iot.wanderlust/" + R.drawable.no_image_found);

        picturesDir = CONTEXT.getApplicationContext().getApplicationContext().getExternalFilesDir("pictures").getAbsolutePath();
        FOLDERS = new String[4];
        FOLDERS[0] = "poi";
        FOLDERS[1] = "tours";
        FOLDERS[2] = "profile";
        FOLDERS[3] = "equipment";
        standardWidth = 1024;

        File pictures = new File(picturesDir);
        if(!pictures.exists()) pictures.mkdir();
        for (String FOLDER : FOLDERS) {
            File dir = new File(picturesDir + "/" + FOLDER);
            dir.mkdir();
        }

        File httpCacheDirectory = new File(DatabaseController.getMainContext().getCacheDir(), "picasso-cache");
        this.cache = new Cache(httpCacheDirectory, 15 * 1024 * 1024);
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
        if(imageInfos != null) {
            for (ImageInfo imageInfo : imageInfos) {
                images.add(new File(picturesDir + "/" + imageInfo.getLocalPath()));
            }
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

    public void delete(ImageInfo imageInfo){
        File f = new File(imageInfo.getLocalPath());
        f.delete();
    }

    public String getPicturesDir() {
        return picturesDir;
    }

    public File resize(File imgFileOrig) throws IOException {
        return resize(imgFileOrig, standardWidth);
    }

    private File resize(File imgFileOrig, int destWidth) throws IOException {
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

    /**
     * get URL for given tour
     * @param tour for searching
     * @return generated URL
     */
    public String getURLImageTourSingle(Tour tour){
        return (tour.getImagePaths().size() > 0) ?
                ServiceGenerator.API_BASE_URL + "/tour/" + tour.getTour_id() + "/img/" + tour.getImagePaths().get(0).getId() :
                defaultImageURL.toString();
    }

    /**
     * get URL for given poi
     * @param poi for searching
     * @return generated URL
     */
    public String getURLImagePOISingle(Poi poi){
        return (poi.getImagePaths().size() > 0) ?
                ServiceGenerator.API_BASE_URL + "/poi/" + poi.getPoi_id() + "/img/1" :
                defaultImageURL.toString();
    }

    public Picasso getPicassoHandler(Activity context) {

        OkHttpClient client = new OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Cookie", LoginUser.getCookies().get(0))
                            .build();
                    return chain.proceed(newRequest);
                })
                .writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();


        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }
    public Bitmap resize(Bitmap b, int destWidth){
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();

        if(origWidth > destWidth){
            int destHeight = origHeight/( origWidth / destWidth ) ;
            Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            b2.compress(Bitmap.CompressFormat.JPEG,80 , outStream);
            return b2;
        }
        return b;
    }

    //https://stackoverflow.com/questions/12369138/disable-android-image-auto-rotate
    public void setAndSaveCorrectOrientation(Bitmap imageBitmap, Uri selectedImage, File path) {
        int orientation = 0;
        final String[] projection = new String[]{MediaStore.Images.Media.ORIENTATION};
        final Cursor cursor = CONTEXT.getContentResolver().query(selectedImage, projection, null, null, null);
        if(cursor != null) {
            final int orientationColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.ORIENTATION);
            if(cursor.moveToFirst()) {
                orientation = cursor.isNull(orientationColumnIndex) ? 0 : cursor.getInt(orientationColumnIndex);
            }
            cursor.close();
        }
        if(orientation == 0) return;

        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, false);
        try {
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

}

