package eu.wise_iot.wanderlust.utils;

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

import eu.wise_iot.wanderlust.MapFragment;
import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.constants.Constants;
import eu.wise_iot.wanderlust.constants.Defaults;
import eu.wise_iot.wanderlust.service.FeedbackService;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MyCamera:
 * @author Fabian Schwander
 * @license MIT
 */
public class MyCamera {
    private static final String TAG = "MyCamera";
    private Activity activity;
    private MapFragment mapFragment;

    private String imagePath;
    private String imageName;
    private File photoFile;
    private Uri imageUri;

    public MyCamera(Activity activity, MapFragment fragment) {
        this.activity = activity;
        this.mapFragment = fragment;
    }

    public void start() {
        dispatchPictureIntent();
        addPictureToGallery();
//        uploadFile();
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
            if (photoFile != null) {
                // FileProvider is needed for targetSDKVersion >= 24
                imageUri = FileProvider.getUriForFile(activity, "eu.wise_iot.wanderlust.utils.MyCamera", photoFile);

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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "img_" + timeStamp + "_";
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

    private void uploadFile() { // FIXME: does not work yet
        RequestBody filePart = RequestBody.create(
                MediaType.parse("image/*"),
                photoFile);
        MultipartBody.Part file = MultipartBody.Part.createFormData("photo", imageName, filePart);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(Defaults.URL_SERVER)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        FeedbackService service = retrofit.create(FeedbackService.class);
        Call<ResponseBody> call = service.uploadPhoto(file);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(activity, R.string.msg_photo_upload_successful, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "photo saved and response received: " + response.isSuccessful());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(activity, R.string.msg_photo_upload_failed, Toast.LENGTH_SHORT).show();
                Log.e(TAG, t.getMessage());
            }
        });
    }

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return imagePath;
    }
}