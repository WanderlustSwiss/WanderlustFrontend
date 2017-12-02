package eu.wise_iot.wanderlust.controllers;

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

    private PoiFragment fragment;
    private PoiTypeDao poiTypeDao;
    private PoiDao poiDao;

    public PoiController(PoiFragment fragment){
        this.fragment = fragment;
        poiTypeDao = new PoiTypeDao(MainActivity.boxStore);
        poiDao = new PoiDao(MainActivity.boxStore);
    }

    public List<PoiType> getAllPoiTypes(){
        return poiTypeDao.find();
    }

    public void savePoiToDatabase(String poiTitle, String poiDescription, String poiPrivacy,
                                  String poiType, String poiPicture){

    }

    public void saveNewPoi(Poi poi, FragmentHandler handler){
        poiDao.create(poi, handler);
    }

    public void uploadImage(File image, int poiID, FragmentHandler handler){
        poiDao.addImage(image, poiID, handler);
    }

    public void downloadImage(int poiID, int imageID, FragmentHandler handler){
        poiDao.getImage(poiID, imageID, handler);
    }

    public void deleteImage(int poiID, int imageID, FragmentHandler handler){
        poiDao.deleteImage(poiID, imageID, handler);
    }

}
