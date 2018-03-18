package eu.wise_iot.wanderlust.controllers;


import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType_;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;


/**
 * Handles the communication between the fragments and the
 * frontend & backend database
 *
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class PoiController {

    private PoiTypeDao poiTypeDao;
    private PoiDao poiDao;
    private UserDao userDao;
    private ImageController imageController;
    private Context context;

    public PoiController(){
        poiTypeDao = PoiTypeDao.getInstance();
        poiDao = PoiDao.getInstance();
        userDao = UserDao.getInstance();
        imageController = ImageController.getInstance();
        context = DatabaseController.getMainContext();
    }

    /**
     * @return List of all poi types
     */
    public List<PoiType> getAllPoiTypes() {
        return poiTypeDao.find();
    }

    /**
     * @return a specific poi type
     */
    public PoiType getType(long poit_id) {
        try {
            return poiTypeDao.findOne(PoiType_.poit_id, poit_id);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return null;
        }
    }


    /**
     * saves a newly generated poi into the database
     *
     * @param poi
     * @param handler
     */
    public void saveNewPoi(Poi poi, FragmentHandler handler) {
        poiDao.create(poi, handler);
    }

    public void updatePoi(Poi poi, FragmentHandler handler) {
        poiDao.update(poi, handler);
    }

    /**
     * Gets a poi by id and returns it in the event
     *
     * @param id
     * @param handler
     */
    public void getPoiById(long id, FragmentHandler handler) {
        poiDao.retrieve(id, handler);
    }

    /**
     * Adds an image to a existing poi and saves it in the database
     *
     * @param image
     * @param poi
     * @param handler
     */
    public void uploadImage(File image, Poi poi, FragmentHandler handler) {
        poiDao.addImage(image, poi, handler);
    }


    /**
     * Returns all images in the event as List<File>
     * if the image already exists on the phone database
     * it will attempt to download it from the backend database
     *
     * @param poi
     * @param handler
     */
    public void getImages(Poi poi, FragmentHandler handler) {
        if (poi.isPublic()) {
            //Download images if necessary
            GetImagesTask imagesTask = new GetImagesTask();
            //CAREFUL asynchron task, will fire the handler
            imagesTask.execute(new ImagesTaskParameters(poi.getPoi_id(), poi.getImagePaths(), "poi", handler));
        } else {
            //Images should be local
            List<File> images = imageController.getImages(poi.getImagePaths());
            handler.onResponse(new ControllerEvent(EventType.OK, images));
        }
    }

    /**
     * Deletes an image from a specific poi from the database
     *
     * @param poiID
     * @param imageID
     * @param handler
     */
    public void deleteImage(long poiID, long imageID, FragmentHandler handler) {
        poiDao.deleteImage(poiID, imageID, handler);
    }

    /**
     * Check if user is owner of specific poi
     *
     * @param poi Poi:poi to check
     * @return boolean:true if user is owner
     */
    public boolean isOwnerOf(Poi poi) {
        long thisUserId = userDao.getUser().getUser_id();
        long userId = poi.getUser();
        return thisUserId == userId;
    }

    /**
     * Deletes a Poi from the database
     *
     * @param poi
     * @param handler
     */
    public void deletePoi(Poi poi, FragmentHandler handler) {
        poiDao.delete(poi, handler);
    }


    /**
     * Shares image on instagram
     *
     */
    public File getImageToShare(Poi poi) {
        List<File> images = imageController.getImages(poi.getImagePaths());
        if (images != null && images.size() > 0){
            return images.get(0);
        }else{
            return null;
        }
    }

}
