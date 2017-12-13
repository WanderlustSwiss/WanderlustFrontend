package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;

import org.osmdroid.util.BoundingBox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.views.MainActivity;
import io.objectbox.Box;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * PoiDao:
 * <p>
 * Represents POI controller
 *
 * @author Rilind Gashi, Alexander Weinbeck, Tobias Rüegsegger
 * @license MIT
 */

public class PoiDao extends DatabaseObjectAbstract {
    public Box<Poi> poiBox;
    public static PoiService service;

    /**
     * constructor
     */

    public PoiDao() {
        poiBox = DatabaseController.boxStore.boxFor(Poi.class);
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
                    try {
                        Poi internalPoi = findOne(Poi_.poi_id, id);
                        Poi backendPoi = response.body();
                        if (response.body().isPublic()) {
                            for (Poi.ImageInfo imageInfo : backendPoi.getImagePaths()) {
                                //count will be increases automatically
                                backendPoi.addImageId((byte) imageInfo.getId());
                            }
                            backendPoi.setInternal_id(0);
                        } else {
                            //imagepaths will always be empty
                            backendPoi.setInternal_id(internalPoi.getInternal_id());
                            backendPoi.setImageIds(internalPoi.getImageIds(), internalPoi.getImageCount());
                        }
                        poiBox.put(backendPoi);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendPoi));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
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


    /**
     * get all pois out of the remote database by entity
     *
     * @param handler
     */
    public void retrieveAll(final FragmentHandler handler) {
        Call<List<Poi>> call = service.retrieveAllPois();
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {

                    //TODO response list
                    //handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body());
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
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
                    try {
                        Poi backendPoi = response.body();
                        Poi internalPoi = findOne(Poi_.poi_id, poi.getPoi_id());
                        backendPoi.setInternal_id(internalPoi.getInternal_id());
                        backendPoi.setImageIds(internalPoi.getImageIds(), internalPoi.getImageCount());
                        poiBox.put(backendPoi);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendPoi));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
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
                    poiBox.remove(poi);
                    DatabaseController.deletePoiImages(poi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                    DatabaseController.sync(new DatabaseEvent(DatabaseEvent.SyncType.POIAREA));
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
     * @param file
     * @param poi
     */
    public void addImage(final File file, final Poi poi, final FragmentHandler handler) {

        if (poi.isPublic()) {
            //Upload image to backend
            PoiService service = ServiceGenerator.createService(PoiService.class);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            Call<Poi.ImageInfo> call = service.uploadImage(poi.getPoi_id(), body);
            call.enqueue(new Callback<Poi.ImageInfo>() {
                @Override
                public void onResponse(Call<Poi.ImageInfo> call, Response<Poi.ImageInfo> response) {
                    if (response.isSuccessful()) {
                        try {
                            Poi internalPoi = findOne(Poi_.poi_id, poi.getPoi_id());
                            Poi.ImageInfo imageInfo = response.body();
                            String name = poi.getPoi_id() + "-" + imageInfo.getId();
                            saveImageOnApp(name, file);
                            internalPoi.addImageId((byte) imageInfo.getId());
                            poiBox.put(internalPoi);
                            handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalPoi));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                        }
                    } else {
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                    }
                }

                @Override
                public void onFailure(Call<Poi.ImageInfo> call, Throwable t) {
                    handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
                }
            });
        } else {
            try {
                Poi internalPoi = findOne(Poi_.poi_id, poi.getPoi_id());
                byte id = (byte) (internalPoi.getImageCount() + 1);
                internalPoi.addImageId(id);
                String name = internalPoi.getPoi_id() + "-" + id + ".jpg";
                saveImageOnApp(name, file);
                poiBox.put(internalPoi);
                handler.onResponse(new ControllerEvent(EventType.OK, internalPoi));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        }
    }

    public void saveImageOnApp(String name, File file) throws IOException {

        InputStream in = new FileInputStream(file);
        FileOutputStream out = DatabaseController.mainContext.openFileOutput(name, Context.MODE_PRIVATE);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        in.close();
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
        Call<Poi.ImageInfo> call = service.deleteImage(poiID, imageID);
        call.enqueue(new Callback<Poi.ImageInfo>() {
            @Override
            public void onResponse(Call<Poi.ImageInfo> call, Response<Poi.ImageInfo> response) {
                if (response.isSuccessful()) {
                    try {
                        Poi internalPoi = findOne(Poi_.poi_id, poiID);
                        if (!internalPoi.removeImageId((byte)response.body().getId())){
                            //TODO image id not found
                        }
                        poiBox.put(internalPoi);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), internalPoi));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Poi.ImageInfo> call, Throwable t) {
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
    public Poi findOne(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return poiBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Poi findOne(Property searchedColumn, long searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return poiBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Poi> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Poi> find(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return poiBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Poi> find(Property searchedColumn, long searchPattern) throws NoSuchFieldException, IllegalAccessException {
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

    public void syncPois() {
        Call<List<Poi>> call = service.retrieveAllPois();
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    poiBox.remove(find(Poi_.isPublic, true));
                    for(Poi poi : response.body()){
                        if(poi.isPublic()) {
                            poi.setInternal_id(0);
                            poiBox.put(poi);
                        }
                    }

                }
                DatabaseController.syncPoisDone();
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                DatabaseController.syncPoisDone();
            }
        });
    }

    public void syncPois(BoundingBox box) {
        Call<List<Poi>> call = service.retrievePoisByArea(
                box.getLatNorth(), box.getLonWest(), box.getLatSouth(), box.getLonEast());
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    poiBox.remove(find(Poi_.isPublic, true));
                    for(Poi poi : response.body()){
                        if(poi.isPublic()) {
                            poi.setInternal_id(0);
                            for (Poi.ImageInfo imageInfo : poi.getImagePaths()) {
                                poi.addImageId((byte) imageInfo.getId());
                            }
                            poiBox.put(poi);
                        }
                    }

                }
                DatabaseController.syncPoisDone();
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                DatabaseController.syncPoisDone();
            }
        });
    }


}
