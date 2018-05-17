package eu.wise_iot.wanderlust.models.DatabaseObject;

import org.osmdroid.util.BoundingBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PoiDao:
 * Represents POI controller
 *
 * @author Rilind Gashi, Alexander Weinbeck, Tobias Rüegsegger, Simon Kaspar
 * @license MIT
 */

public class PoiDao extends DatabaseObjectAbstract {

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    private static class Holder {
        private static final PoiDao INSTANCE = new PoiDao();
    }

    public static PoiDao getInstance() {
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private static PoiService service;
    private final Box<Poi> poiBox;
    private final PoiController poiController = new PoiController();

    /**
     * constructor
     */

    private PoiDao() {
        poiBox = BOXSTORE.boxFor(Poi.class);
        service = ServiceGenerator.createService(PoiService.class);
    }

    /**
     * Insert a poi into the database
     *
     * @param poi     (required)
     * @param handler
     */
    public void create(final Poi poi, final FragmentHandler handler) {

        Call<Poi> call = service.createPoi(poi);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, retrofit2.Response<Poi> response) {
                if (response.isSuccessful()) {
                    Poi newPoi = response.body();
                    newPoi.setInternal_id(0);
                    poiBox.put(newPoi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), newPoi));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * get poi out of the remote database by entity
     *
     * @param id
     * @param handler
     */
    public void retrieve(long id, final FragmentHandler handler) {
        Call<Poi> call = service.retrievePoi(id);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if (response.isSuccessful()) {
                    Poi internalPoi = findOne(Poi_.poi_id, id);
                    Poi backendPoi = response.body();
                    if (response.body().isPublic()) {
                        backendPoi.setImagePaths(internalPoi.getImagePaths());
                        backendPoi.setInternal_id(0);
                        poiBox.remove(internalPoi.getInternal_id());
                    } else {
                        //imagepaths will always be empty
                        backendPoi.setInternal_id(internalPoi.getInternal_id());
                        backendPoi.setImagePaths(internalPoi.getImagePaths());
                    }
                    poiBox.put(backendPoi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendPoi));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void retrieveUserPois() {
        Call<List<Poi>> call = service.retrieveAllPois();
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {

                List<Poi> userPois = response.body();

                for (Poi poi : userPois) {
                    if (findOne(Poi_.poi_id, poi.getPoi_id()) == null) {
                        //new UserPoi
                        poi.setInternal_id(0);
                        poiBox.put(poi);
                        poiController.getImages(poi, controllerEvent -> {
                            switch (controllerEvent.getType()) {
                                case OK:
                                    break;
                                default:

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {

            }
        });
    }

    /**
     * update:
     * update a poi in the database
     *
     * @param handler
     */
    public void update(Poi poi, final FragmentHandler handler) {
        Call<Poi> call = service.updatePoi(poi.getPoi_id(), poi);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                //backend will not look at images
                if (response.isSuccessful()) {
                    Poi backendPoi = response.body();
                    Poi internalPoi = findOne(Poi_.poi_id, poi.getPoi_id());
                    backendPoi.setInternal_id(internalPoi.getInternal_id());
                    backendPoi.setImagePaths(internalPoi.getImagePaths());
                    poiBox.put(backendPoi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendPoi));
                    DatabaseController.getInstance().sync(new DatabaseEvent(DatabaseEvent.SyncType.EDITSINGLEPOI, backendPoi));

                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * delete a poi in the database
     *
     * @param poi
     * @param handler
     */
    public void delete(final Poi poi, final FragmentHandler handler) {
        Call<Poi> call = service.deletePoi(poi.getPoi_id());
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if (response.isSuccessful()) {
                    poiBox.remove(find(Poi_.poi_id, poi.getPoi_id()));
                    for (ImageInfo imageInfo : poi.getImagePaths()) {
                        new File(imageInfo.getLocalPath()).delete();
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                    DatabaseController.getInstance().sync(new DatabaseEvent(DatabaseEvent.SyncType.DELETESINGLEPOI, response.body()));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * add an image to the db
     *
     * @param origFile
     * @param poi
     */
    public void addImage(final File origFile, final Poi poi, final FragmentHandler handler) {
        ImageController imageController = ImageController.getInstance();
        try {
            File file = imageController.resize(origFile);
            if (poi.isPublic()) {
                //Upload image to backend
                PoiService service = ServiceGenerator.createService(PoiService.class);
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
                Call<ImageInfo> call = service.uploadImage(poi.getPoi_id(), body);
                call.enqueue(new Callback<ImageInfo>() {
                    @Override
                    public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
                        if (response.isSuccessful()) {
                            try {
                                Poi internalPoi = findOne(Poi_.poi_id, poi.getPoi_id());
                                ImageInfo imageInfo = response.body();
                                imageInfo.setName(poi.getPoi_id() + "-" + imageInfo.getId() + ".jpg");
                                imageInfo.setLocalDir(imageController.getPoiFolder());
                                imageController.save(file, imageInfo);
                                internalPoi.addImagePath(imageInfo);
                                poiBox.put(internalPoi);
                                handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalPoi));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                        }
                    }

                    @Override
                    public void onFailure(Call<ImageInfo> call, Throwable t) {
                        handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
                    }
                });
            } else {
                Poi internalPoi = findOne(Poi_.poi_id, poi.getPoi_id());
                String name = internalPoi.getPoi_id() + "-1.jpg";
                ImageInfo newImage = new ImageInfo(1, name, imageController.getPoiFolder());
                imageController.save(file, newImage);
                internalPoi.addImagePath(newImage);
                poiBox.put(internalPoi);
                handler.onResponse(new ControllerEvent(EventType.OK, poi));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes an image from a specific poi from the database
     * and return it in the event
     *
     * @param poiID
     * @param imageID
     * @param handler
     */
    public void deleteImage(final long poiID, final long imageID, final FragmentHandler handler) {
        Call<ImageInfo> call = service.deleteImage(poiID, imageID);
        call.enqueue(new Callback<ImageInfo>() {
            @Override
            public void onResponse(Call<ImageInfo> call, Response<ImageInfo> response) {
                if (response.isSuccessful()) {
                    Poi internalPoi = findOne(Poi_.poi_id, poiID);
                    internalPoi.removeImage(internalPoi.getImageById(imageID));
                    poiBox.put(internalPoi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalPoi));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<ImageInfo> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * returns a list with all poi from the frontend database
     *
     * @return List<Poi>
     */
    public List<Poi> find() {
        return poiBox.getAll();
    }

    /**
     * Searching for a single user with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return User who match to the search pattern in the searched columns
     */
    public Poi findOne(Property searchedColumn, String searchPattern) {
        return poiBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Poi findOne(Property searchedColumn, long searchPattern) {
        return poiBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Poi> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Poi> find(Property searchedColumn, String searchPattern) {
        return poiBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Poi> find(Property searchedColumn, long searchPattern) {
        return poiBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Poi> find(Property searchedColumn, boolean searchPattern) {
        return poiBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    /**
     * delete by pattern
     *
     * @param searchedColumn
     * @param searchPattern
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void delete(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        poiBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void delete(Property searchedColumn, long searchPattern) throws NoSuchFieldException, IllegalAccessException {
        poiBox.remove(findOne(searchedColumn, searchPattern));
    }

    /**
     * delete all poi
     */
    public void deleteAll() {
        poiBox.removeAll();
    }

    /**
     * count all poi
     *
     * @return Total number of records
     */
    public long count() {
        return poiBox.count();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Total number of records
     */
    public long count(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    public long count(Property searchedColumn, long searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }


    public void syncPois(BoundingBox box) {
        ImageController imageController = ImageController.getInstance();
        Call<List<Poi>> call = service.retrievePoisByArea(
                box.getLatNorth(), box.getLonWest(), box.getLatSouth(), box.getLonEast());
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    for (Poi poi : response.body()) {
                        //if (poi.isPublic()) {
                        poi.setInternal_id(0);
                        Poi internalPoi = findOne(Poi_.poi_id, poi.getPoi_id());
                        if (internalPoi == null) {
                            //non existent localy
                            List<ImageInfo> imageInfos = new ArrayList<>();
                            for (ImageInfo imageInfo : poi.getImagePaths()) {
                                String name = poi.getPoi_id() + "-" + imageInfo.getId() + ".jpg";
                                imageInfos.add(new ImageInfo(poi.getPoi_id(), name, imageController.getPoiFolder()));
                            }
                            poi.setImagePaths(imageInfos);
                            poi.setInternal_id(0);
                        } else {
                            poi.setImagePaths(internalPoi.getImagePaths());
                            poiBox.remove(internalPoi.getInternal_id());
                        }
                        poiBox.put(poi);
                        //}
                    }

                }
                DatabaseController.getInstance().syncPoisDone();
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                DatabaseController.getInstance().syncPoisDone();
            }
        });
    }

    /**
     * Delete all users out of the database
     */
    public void removeAll() {
        poiBox.removeAll();
    }

    public void removeNonUserPois(long userId) {
        List<Poi> userPois = poiBox.find(Poi_.user, userId);
        for (Poi p : userPois) {
            p.setInternal_id(0);
        }
        poiBox.removeAll();
        poiBox.put(userPois);
    }

}
