package eu.wise_iot.wanderlust.controllers;

import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.services.ImageService;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * The asynchronous task for receiving all images from a specific poi
 */
public class GetImagesTask extends AsyncTask<ImagesTaskParameters, Void, List<File>> {

    private FragmentHandler handler;
    private List<DownloadedImage> downloadedImages = new LinkedList<>();

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


        ImageService service = ServiceGenerator.createService(ImageService.class);

        List<File> images = new ArrayList<>();
        for(ImageInfo imageInfo : imageInfos){
            File image = new File(ImageController.picturesDir + "/" + imageInfo.getPath());
            if (!image.exists()) {
                //Download it!
                try {
                    Call call = service.downloadImage(route, id, imageInfo.getId());
                    Response<ResponseBody> response = call.execute();
                    if (response.isSuccessful()) {
                        ResponseBody downloadedImg = response.body();
                        writeToDisk(downloadedImg, image.getAbsolutePath());
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
        DatabaseController.addDownloadedImages(downloadedImages);
        handler.onResponse(new ControllerEvent<>(EventType.OK, images));
    }

    /**
     * Writes a downloaded poi to the phone and names it correctly
     *
     * @param body    which represents the image downloaded
     * @return true if everything went ok
     */
    private boolean writeToDisk(ResponseBody body, String path) {

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            byte[] fileReader = new byte[4096];

            long fileSizeDownloaded = 0;
            inputStream = body.byteStream();
            outputStream = new FileOutputStream(path);


            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

                fileSizeDownloaded += read;

            }

            outputStream.flush();
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null)
                    outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

