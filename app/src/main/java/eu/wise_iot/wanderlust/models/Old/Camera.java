package eu.wise_iot.wanderlust.models.Old;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.views.MapFragment;

/**
 * Camera related actions
 *
 * @author Fabian Schwander
 * @license MIT
 */
public class Camera {
    private static final String TAG = "Camera";
    private final Activity activity;
    private final MapFragment mapFragment;

    private String imagePath;
    private String imageName;
    private File photoFile;

    public Camera(Activity activity, MapFragment fragment) {
        this.activity = activity;
        mapFragment = fragment;
    }

    public void start() {
        dispatchPictureIntent();
        addPictureToGallery();
    }

    private void dispatchPictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // check if device has camera
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(activity, R.string.msg_picture_not_saved, Toast.LENGTH_LONG).show();
            }
            if (photoFile != null && activity != null) {
                // FileProvider is needed for targetSDKVersion >= 24
                Uri imageUri = FileProvider.getUriForFile(activity, "eu.wise_iot.wanderlust.models.Old.Camera", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                mapFragment.startActivityForResult(takePictureIntent, Constants.TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // set up storage dir
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory");
                return null;
            }
        }
        // set up file name
        //TODO: add locale to SimpleDateFormat (Multilingual)
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "img_" + timeStamp + '_';
        String suffix = ".jpg";

        File image = File.createTempFile(fileName, suffix, storageDir);
        imageName = image.getName();
        imagePath = image.getAbsolutePath();
        photoFile = image;

        return image;
    }

    private void addPictureToGallery() {


        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(photoFile));
        activity.sendBroadcast(mediaScanIntent);

    }

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return imagePath;
    }
}