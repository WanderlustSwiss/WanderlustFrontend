package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.util.Log;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * PoiDao:
 *
 * Represents POI controller
 * @author Rilind Gashi, Alexander Weinbeck
 * @license MIT
 */

public class PoiDao extends DatabaseObjectAbstract{
    private Box<Poi> poiBox;
    private Query<Poi> poiQuery;
    private QueryBuilder<Poi> poiQueryBuilder;
    Property columnProperty;
    private static PoiService service;

    /**
     * constructor
     * @param boxStore (required) delivers the connection to the frontend database
     * @param context
     */

    public PoiDao(BoxStore boxStore){
        poiBox = boxStore.boxFor(Poi.class);
        poiQueryBuilder = poiBox.query();

        if(service == null){
    public long count(){
            service = ServiceGenerator.createService(PoiService.class);
        }
        poiQueryBuilder.equal(columnProperty , searchPattern);
    }

    /**
     * Insert a poi into the database
     * @param poi (required)
     * @param handler
     *
     */
    public Poi update(Poi poi){
    public void create(final AbstractModel poi, final FragmentHandler handler) {
        Call<Poi> call = service.createPoi((Poi)poi);
             @Override
             public void onResponse(Call<Poi> call, Response<Poi> response) {
                 if(response.isSuccessful()) {
                    poiBox.put((Poi) poi);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
            }
            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * get poi out of the remote database by entity
     * @param id
     * @param handler
     */
    public void retrieve(int id, final FragmentHandler handler){
        Call<Poi> call = service.retrievePoi(id);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if(response.isSuccessful()){
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                 } else{

                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                 }
             }
             @Override
             public void onFailure(Call<Poi> call, Throwable t) {

                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * get all pois out of the remote database by entity
     * @param handler
     */
    public void retrieveAll(final FragmentHandler handler){
        Call<Poi> call = service.retrieveAllPois();
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if(response.isSuccessful()){
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
             }
    }
    /**
     * update:
     * update a poi in the database
     * @param id
     * @param poi
     * @param handler
     */
    public void update(int id, final AbstractModel poi, final FragmentHandler handler) {
        Call<Poi> call = service.updatePoi(id,(Poi)poi);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if(response.isSuccessful()) {
                    poiBox.put((Poi) poi);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
            }
            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * delete a poi in the database
     * @param poi
     * @param handler
     *
     */
    public void delete(final AbstractModel poi, final FragmentHandler handler) {
        Call<Poi> call = service.deletePoi((Poi)poi);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if(response.isSuccessful()) {
                    poiBox.remove((Poi) poi);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
            }
            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * add an image to the db
     * @param file
     * @param poiID
     */
    @Override
    public void addImage(final File file, final int poiID){
        PoiService service = ServiceGenerator.createService(PoiService.class);
        Call<Poi> call = service.postPoi(poi);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call<Poi> call = service.uploadImage(poiID, body);
        poiBox.put(poi);
    }
    /**
     * returns a list with all poi
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
     * @param searchPattern (required) contain the search pattern.
     *
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
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Poi> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Poi> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi.class);
        poiQueryBuilder.equal(columnProperty , searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.find();
    }

    /**
     * delete by pattern
     * @param searchedColumn
     * @param searchPattern
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        poiBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteAll(){
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


}
