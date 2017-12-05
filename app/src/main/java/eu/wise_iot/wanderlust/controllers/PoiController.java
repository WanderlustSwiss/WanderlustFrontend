package eu.wise_iot.wanderlust.controllers;


import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import okhttp3.ResponseBody;
import retrofit2.Call;


public class PoiController {

    public PoiController() {
    }

    public List<PoiType> getAllPoiTypes() {
        return DatabaseController.poiTypeDao.find();
    }

    public List<PoiType> getTypes() {
        return DatabaseController.poiTypeDao.find();
    }

    public void saveNewPoi(Poi poi, FragmentHandler handler) {
        DatabaseController.poiDao.create(poi, handler);
    }

    public void getPoiById(long id, FragmentHandler handler) {
        DatabaseController.poiDao.retrieve(id, handler);
    }

    public void uploadImage(File image, long poiID, FragmentHandler handler) {
        DatabaseController.poiDao.addImage(image, poiID, handler);
    }

    public void getImages(Poi poi, FragmentHandler handler) {
        List<Poi.ImageInfo> imageInfos = poi.getImagePath();

        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagetoDelete = new File(filepath, "45-1.jpg");
        imagetoDelete.delete();

        GetImagesTask imagesTask = new GetImagesTask();
        imagesTask.execute(new GetImagesTaskParameters(poi.getPoi_id(), imageInfos, handler));
    }

    public void deleteImage(long poiID, long imageID, FragmentHandler handler) {
        DatabaseController.poiDao.deleteImage(poiID, imageID, handler);
    }


    private static class GetImagesTaskParameters {
        long poiId;
        List<Poi.ImageInfo> imageInfos;
        FragmentHandler handler;

        GetImagesTaskParameters(long poiId, List<Poi.ImageInfo> imageInfos, FragmentHandler handler) {
            this.poiId = poiId;
            this.imageInfos = imageInfos;
            this.handler = handler;
        }
    }

    class GetImagesTask extends AsyncTask<GetImagesTaskParameters, Void, List<File>> {

        private FragmentHandler handler;

        @Override
        protected List<File> doInBackground(GetImagesTaskParameters... parameters) {

            long poiId = parameters[0].poiId;
            List<Poi.ImageInfo> imageInfos = parameters[0].imageInfos;
            handler = parameters[0].handler;

            List<File> images = new ArrayList<>(imageInfos.size());
            for (Poi.ImageInfo imageinfo : imageInfos) {
                File image = imageinfo.getImage();
                if (image.exists()) {
                    //looks like we already have it
                    images.add(image);
                } else {
                    //Download it!
                    Call<ResponseBody> call = PoiDao.service.downloadImage(poiId, imageinfo.getId());
                    try {
                        ResponseBody downloadedImg = call.execute().body();
                        if (writeToDisk(downloadedImg, poiId, imageinfo.getId())) {
                            images.add(image);
                        }
                    } catch (IOException e) {
                        //What if failed?
                        e.printStackTrace();
                    }
                }
            }
            return images;
        }

        @Override
        protected void onPostExecute(List<File> images) {
            handler.onResponse(new Event<List<File>>(EventType.OK, images));
        }

        private boolean writeToDisk(ResponseBody body, long poiId, long imageId) {


            File filepath = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    poiId + "-" + imageId + ".jpg");

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096]; //Giele machet ned zu krassi bilder suscht bricht das zäme
                //if pictures get too large, need to implement a stream:
                // https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(filepath);

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


}
