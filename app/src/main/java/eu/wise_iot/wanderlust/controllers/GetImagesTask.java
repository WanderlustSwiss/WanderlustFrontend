package eu.wise_iot.wanderlust.controllers;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.services.ImageService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * The asynchronous task for receiving all images from a specific poi
 */
public class GetImagesTask extends AsyncTask<ImagesTaskParameters, Void, List<File>> {

    private FragmentHandler handler;
    @SuppressWarnings("FieldCanBeLocal")
    private ImageController imageController;
    private final List<DownloadedImage> downloadedImages = new LinkedList<>();

    /**
     * Task which iterates over all images of a specific poi
     * and checks if it was already downloaded in the frontend database.
     * if the images doesn't exists it will attempt to download it.
     *
     * @param parameters which are used for the task
     * @return all images from a specific poi
     */
    @Override
    protected List<File> doInBackground(ImagesTaskParameters... parameters) {
        long id = parameters[0].id;
        List<ImageInfo> imageInfos = parameters[0].imageInfos;
        String route = parameters[0].route;
        handler = parameters[0].handler;
        imageController = ImageController.getInstance();

        ImageService service = ServiceGenerator.createService(ImageService.class);

        List<File> images = new ArrayList<>();
        for(ImageInfo imageInfo : imageInfos){
            imageInfo.setLocalDir(route);
            File image = new File(imageController.getPicturesDir() + '/' + imageInfo.getLocalPath());
            if (!image.exists()) {
                //Download it!
                try {
                    Call call = service.downloadImage(route, id, 1);
                    Response<ResponseBody> response = call.execute();
                    if (response.isSuccessful()) {
                        ResponseBody downloadedImg = response.body();
                        imageController.save(downloadedImg.byteStream(), imageInfo);
                        downloadedImages.add(new DownloadedImage(image, downloadedImg.contentLength(), true));
                        images.add(image);
                    }
                } catch (IOException e) {
                    //What if failed?
                    e.printStackTrace();
                }
            } else {
                images.add(image);
            }
        }
        return images;
    }

    @Override
    protected void onPostExecute(List<File> images) {
        DatabaseController.getInstance().addDownloadedImages(downloadedImages);
        handler.onResponse(new ControllerEvent<>(EventType.OK, images));
    }
}

