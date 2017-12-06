package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.os.Environment;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
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
    private Box<Poi> poiBox;
    private Query<Poi> poiQuery;
    private QueryBuilder<Poi> poiQueryBuilder;
    Property columnProperty;
    public static PoiService service;

    /**
     * constructor
     */

    public PoiDao() {
        poiBox = DatabaseController.boxStore.boxFor(Poi.class);
        poiQueryBuilder = poiBox.query();
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
                    poi.setPoi_id(response.body().getPoi_id());
                    poiBox.put(poi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), poi));
                } else handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
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
            public void onResponse(Call<Poi> call, retrofit2.Response<Poi> response) {
                if (response.isSuccessful()) {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
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
     * @param id
     * @param poi
     * @param handler
     */
    public void update(int id, final AbstractModel poi, final FragmentHandler handler) {
        Call<Poi> call = service.updatePoi(id, (Poi) poi);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if (response.isSuccessful()) {
                    poiBox.put((Poi) poi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
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
        Call<Poi> call = service.deletePoi(poi);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if (response.isSuccessful()) {
                    poiBox.remove(poi);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
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
     * @param poiID
     */
    public void addImage(final File file, final long poiID, final FragmentHandler handler) {
        PoiService service = ServiceGenerator.createService(PoiService.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call<Poi.ImageInfo> call = service.uploadImage(poiID, body);
        call.enqueue(new Callback<Poi.ImageInfo>() {
            @Override
            public void onResponse(Call<Poi.ImageInfo> call, Response<Poi.ImageInfo> response) {
                if (response.isSuccessful()) {

                    File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                    if (!filepath.exists()) {
                        ;
                        if (!filepath.mkdirs()) {
                            ;
                        }
                    }
                    File image = new File(filepath, response.body().getName());
                    file.renameTo(image);
                }
                handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Poi.ImageInfo> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * deletes an image from a specific poi from the database
     * and return it in the event
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
                    Poi poi = poiBox.get(poiID);

                    Poi.ImageInfo imageInfoToDelete = null;
                    for (Poi.ImageInfo imageInfo : poi.getImagePath()) {
                        if (imageInfo.getId() == imageID) {
                            imageInfoToDelete = imageInfo;
                            break;
                        }
                    }
                    if (imageInfoToDelete == null) {
                        //TODO image not found
                    } else {
                        poi.getImagePath().remove(imageInfoToDelete);
                        poiBox.put(poi);
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
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
    public Poi findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi.class);
        poiQueryBuilder.equal(columnProperty, searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Poi> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Poi> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi.class);
        poiQueryBuilder.equal(columnProperty, searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.find();
    }

    /**
     * delete by pattern
     *
     * @param searchedColumn
     * @param searchPattern
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        poiBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteAll() {
        poiBox.removeAll();
    }

    public long count() {
        return poiBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi.class);
        poiQueryBuilder.equal(columnProperty, searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.find().size();
    }

    public void syncPois(){
        Call<List<Poi>> call = service.retrieveAllPois();
        call.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if(response.isSuccessful()){
                    poiBox.removeAll();
                    poiBox.put(response.body());
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
