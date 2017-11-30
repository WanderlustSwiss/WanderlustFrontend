package eu.wise_iot.wanderlust.controllers;

import android.content.Context;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiTypeDao;
import io.objectbox.BoxStore;


public class PoiController {

    private BoxStore boxStore;
    private PoiTypeDao poiTypeDao;

    public PoiController(Context context){
        boxStore = MyObjectBox.builder().androidContext(context).build();
        poiTypeDao = new PoiTypeDao(boxStore);
        /*
        PoiType poiTypeOne = new PoiType(0, "Sehenswürdigkeit");
        PoiType poiTypeTwo = new PoiType(0, "Berg");
        PoiType poiTypeThree = new PoiType(0, "Restaurant");
        PoiType poiTypeFour = new PoiType(0, "Gewässer");
        poiTypeDao.create(poiTypeOne);
        poiTypeDao.create(poiTypeTwo);
        poiTypeDao.create(poiTypeThree);
        poiTypeDao.create(poiTypeFour);
        */
    }

    public List<PoiType> getAllPoiTypes(){
        return poiTypeDao.find();
    }

    public void savePoiToDatabase(String poiTitle, String poiDescription, String poiPrivacy,
                                  String poiType, String poiPicture){

    }
}
