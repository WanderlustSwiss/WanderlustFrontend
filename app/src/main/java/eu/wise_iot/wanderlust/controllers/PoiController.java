package eu.wise_iot.wanderlust.controllers;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import okhttp3.ResponseBody;
import retrofit2.Call;


/**
 * Handles the communication between the fragments and the
 * frontend & backend database
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class PoiController {

    public PoiController() {}

    /**
     * @return List of all poi types
     */
    public List<PoiType> getAllPoiTypes() {
        return DatabaseController.poiTypeDao.find();
    }

    /**
     * @return a specific poi type
     */
    public PoiType getType(long poit_id) {
        try {
            return DatabaseController.poiTypeDao.findOne(PoiType_.poit_id, poit_id);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }


    /**
     * saves a newly generated poi into the database
     * @param poi
     * @param handler
     */
    public void saveNewPoi(Poi poi, FragmentHandler handler) {
        DatabaseController.poiDao.create(poi, handler);
    }

    /**
     * Gets a poi by id and returns it in the event
     * @param id
     * @param handler
     */
    public void getPoiById(long id, FragmentHandler handler) {
        DatabaseController.poiDao.retrieve(id, handler);
    }

    /**
     * Adds an image to a existing poi and saves it in the database
     * @param image
     * @param poi
     * @param handler
     */
    public void uploadImage(File image, Poi poi, FragmentHandler handler) {
        DatabaseController.poiDao.addImage(image, poi, handler);
    }

    /**
     * Returns all images in the event as List<File>
     * if the image already exists on the phone database
     * it will attempt to download it from the backend database
     * @param poi
     * @param handler
     */
    public void getImages(Poi poi, FragmentHandler handler) {
        List<Poi.ImageInfo> imageInfos = poi.getImagePath();
        if(poi.isPublic()) {
            //Download images if necessary
            GetImagesTask imagesTask = new GetImagesTask();
            imagesTask.execute(new GetImagesTaskParameters(poi.getPoi_id(), imageInfos, handler));
        } else{
            //Images should be local
            List<File> images = new ArrayList<>();
            for(Poi.ImageInfo imageInfo : imageInfos){
                images.add(imageInfo.getImage());
            }
        }
    }

    /**
     * Deletes an image from a specific poi from the database
     * @param poiID
     * @param imageID
     * @param handler
     */
    public void deleteImage(long poiID, long imageID, FragmentHandler handler) {
        DatabaseController.poiDao.deleteImage(poiID, imageID, handler);
    }

    /**
     * Check if user is owner of specific poi
     * @param poi Poi:poi to check
     * @return boolean:true if user is owner
     */
    public boolean isOwnerOf(Poi poi) {
        long thisUserId = DatabaseController.userDao.find().get(0).getUser_id();
        long userId = poi.getUser();
//        DatabaseController.poiDao.findOne(Poi_.poi_id, id);
//        return thisUserId == userId;

        // todo: remove and replace with return above
        return true;
    }


    /**
     * Parameters for the getImages method
     * if this was c++ i could have just use a tuple
     */
    private class GetImagesTaskParameters {
        long poiId;
        List<Poi.ImageInfo> imageInfos;
        FragmentHandler handler;

        GetImagesTaskParameters(long poiId, List<Poi.ImageInfo> imageInfos, FragmentHandler handler) {
            this.poiId = poiId;
            this.imageInfos = imageInfos;
            this.handler = handler;
        }
    }

    /**
     * The asynchronous task for receiving all images from a specific poi
     */
    class GetImagesTask extends AsyncTask<GetImagesTaskParameters, Void, List<File>> {

        private FragmentHandler handler;

        /**
         * Task which iterates over all images of a specific poi
         * and checks if it was already downloaded in the frontend database.
         * if the images doesn't exists it will attempt to download it.
         * @param parameters which are used for the task
         * @return all images from a specific poi
         */
        @Override
        protected List<File> doInBackground(GetImagesTaskParameters... parameters) {

            long poiId = parameters[0].poiId;
            List<Poi.ImageInfo> imageInfos = parameters[0].imageInfos;
            handler = parameters[0].handler;

            List<File> images = new ArrayList<>(imageInfos.size());
            for (Poi.ImageInfo imageinfo : imageInfos) {
                File image = imageinfo.getImage();
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
            return images;
        }

        @Override
        protected void onPostExecute(List<File> images) {
            handler.onResponse(new ControllerEvent<List<File>>(EventType.OK, images));
        }

        /**
         * Writes a downloaded poi to the phone and names it correctly
         * @param body which represents the image downloaded
         * @param poiId
         * @param imageId
         * @return true if everything went ok
         */
        private boolean writeToDisk(ResponseBody body, long poiId, long imageId) {

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096]; //Giele machet ned zu krassi bilder suscht bricht das zäme
                //if pictures get too large, need to implement a stream:
                // https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                String name = poiId + "-" + imageId + ".jpg";
                inputStream = body.byteStream();
                outputStream = DatabaseController.mainContext.openFileOutput(name, Context.MODE_PRIVATE);


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
