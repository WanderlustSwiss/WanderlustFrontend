package eu.wise_iot.wanderlust.controllers;


import android.content.Context;

import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.io.File;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.ViolationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Handles the communication between the fragments and the
 * frontend & backend database
 *
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class PoiController {


    private final PoiTypeDao poiTypeDao;
    private final PoiDao poiDao;
    private final UserDao userDao;
    private final ImageController imageController;
    private final Context context;
    private final PoiService poiService;
    private static List<Poi> poiCache = new LinkedList<>();

    public PoiController(){
        poiTypeDao = PoiTypeDao.getInstance();
        poiDao = PoiDao.getInstance();
        userDao = UserDao.getInstance();
        imageController = ImageController.getInstance();
        context = DatabaseController.getMainContext();
        poiService = ServiceGenerator.createService(PoiService.class);
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
        return poiTypeDao.findOne(PoiType_.poit_id, poit_id);
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

    public Poi getLocalPoi(long id){
        return poiDao.findOne(Poi_.poi_id, id);
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


    public POI convertPoiToOSMDroidPOI(Poi poi) {
        POI retPOI = new POI((int) poi.getInternal_id());
        retPOI.mId = poi.getPoi_id();
        retPOI.mLocation = new GeoPoint(poi.getLatitude(), poi.getLongitude());
        retPOI.mCategory = String.valueOf(poi.getType());
        retPOI.mType = poi.getTitle();
        retPOI.mDescription = poi.getDescription();

        return  retPOI;
    }

    public List<Poi> getAllPois(){
        return poiDao.find();
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
            imagesTask.execute(new ImagesTaskParameters(poi.getPoi_id(), poi.getImagePaths(), ImageController.getInstance().getPoiFolder(), handler));
        } else {
            //Images should be local
            List<File> images = ImageController.getInstance().getImages(poi.getImagePaths());
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
        poiCache.remove(poi);
        DatabaseController.getInstance().sync(new DatabaseEvent(DatabaseEvent.SyncType.DELETESINGLEPOI));
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

    public void reportViolation(PoiController.Violation violation, final FragmentHandler handler) {
        Call<Void> call = ServiceGenerator.createService(ViolationService.class).sendPoiViolation(violation);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                if (response.isSuccessful())
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * represents a poi violation
     * structure needs to be kept like this for retrofit
     * @author Alexander Weinbeck
     * @license MIT
     */
    public class Violation{
        int poi_id;
        int type;

        public Violation(){
        }
        public Violation(long poi_id, long violationType_id){
            this.poi_id = (int)poi_id;
            this.type = (int)violationType_id;
        }
    }


    public void loadPoiByArea(BoundingBox box) {
        Call<List<Poi>> call = poiService.retrievePoisByArea(
                box.getLatNorth(), box.getLonWest(), box.getLatSouth(), box.getLonEast());
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    poiCache.clear();
                    poiCache.addAll(response.body());
                }
                DatabaseController.getInstance().syncPoisDone();
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                DatabaseController.getInstance().syncPoisDone();
            }
        });
    }

    public List<Poi> getPoiCache() {
        return poiCache;
    }
}
