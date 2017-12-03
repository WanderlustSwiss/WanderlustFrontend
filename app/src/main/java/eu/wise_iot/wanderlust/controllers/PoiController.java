package eu.wise_iot.wanderlust.controllers;

import android.app.DialogFragment;
import android.content.Context;

import java.io.File;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiTypeDao;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.PoiFragment;
import io.objectbox.BoxStore;


public class PoiController {

    private PoiTypeDao poiTypeDao;
    private PoiDao poiDao;

    public PoiController(){
        poiTypeDao = new PoiTypeDao(MainActivity.boxStore);
        poiDao = new PoiDao(MainActivity.boxStore);
    }


    public List<PoiType> getAllPoiTypes(){
        return poiTypeDao.find();
    }

    public void savePoiToDatabase(String poiTitle, String poiDescription, String poiPrivacy,
                                  String poiType, String poiPicture){

    }

    public List<PoiType> getTypes(){
        PoiTypeDao poiTypeDao = new PoiTypeDao(MainActivity.boxStore);
        return poiTypeDao.find();
    }

    public void saveNewPoi(Poi poi, FragmentHandler handler){
        poiDao.create(poi, handler);
    }

    public void getPoiById(long id, FragmentHandler handler){
        poiDao.retrieve(id, handler);
    }

    public void uploadImage(File image, long poiID, FragmentHandler handler){
        poiDao.addImage(image, poiID, handler);
    }

    public void downloadImage(long poiID, long imageID, FragmentHandler handler){
        poiDao.getImage(poiID, imageID, handler);
    }

    public void deleteImage(long poiID, long imageID, FragmentHandler handler){
        poiDao.deleteImage(poiID, imageID, handler);
    }

}
