package eu.wise_iot.wanderlust.controllers;


import java.io.File;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;


public class PoiController {

    public PoiController(){}

    public List<PoiType> getAllPoiTypes(){
        return DatabaseController.poiTypeDao.find();
    }

    public List<PoiType> getTypes(){
        return DatabaseController.poiTypeDao.find();
    }

    public void saveNewPoi(Poi poi, FragmentHandler handler){
        DatabaseController.poiDao.create(poi, handler);
    }

    public void getPoiById(long id, FragmentHandler handler){
        DatabaseController.poiDao.retrieve(id, handler);
    }

    public void uploadImage(File image, long poiID, FragmentHandler handler){
        DatabaseController.poiDao.addImage(image, poiID, handler);
    }

    public void downloadImage(long poiID, long imageID, FragmentHandler handler){
        DatabaseController.poiDao.getImage(poiID, imageID, handler);
    }

    public void deleteImage(long poiID, long imageID, FragmentHandler handler){
        DatabaseController.poiDao.deleteImage(poiID, imageID, handler);
    }

}
