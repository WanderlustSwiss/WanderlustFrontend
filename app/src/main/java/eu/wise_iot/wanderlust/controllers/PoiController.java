package eu.wise_iot.wanderlust.controllers;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType_;


/**
 * Handles the communication between the fragments and the
 * frontend & backend database
 *
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class PoiController {

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
     *
     * @param poi
     * @param handler
     */
    public void saveNewPoi(Poi poi, FragmentHandler handler) {
        DatabaseController.poiDao.create(poi, handler);
    }

    public void updatePoi(Poi poi, FragmentHandler handler) {
        DatabaseController.poiDao.update(poi, handler);
    }

    /**
     * Gets a poi by id and returns it in the event
     *
     * @param id
     * @param handler
     */
    public void getPoiById(long id, FragmentHandler handler) {
        DatabaseController.poiDao.retrieve(id, handler);
    }

    /**
     * Adds an image to a existing poi and saves it in the database
     *
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
            List<File> images = ImageController.getImages(poi.getImagePaths());
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
        DatabaseController.poiDao.deleteImage(poiID, imageID, handler);
    }

    /**
     * Check if user is owner of specific poi
     *
     * @param poi Poi:poi to check
     * @return boolean:true if user is owner
     */
    public boolean isOwnerOf(Poi poi) {
        long thisUserId = DatabaseController.userDao.getUser().getUser_id();
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
        DatabaseController.poiDao.delete(poi, handler);
    }





}
