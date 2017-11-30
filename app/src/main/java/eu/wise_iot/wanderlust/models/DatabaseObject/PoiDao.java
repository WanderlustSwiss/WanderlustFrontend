package eu.wise_iot.wanderlust.models.DatabaseObject;


import android.accounts.NetworkErrorException;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi_;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.views.MainActivity;
import io.objectbox.Box;
import io.objectbox.BoxStore;
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
 * PoiDao
 *
 * @author Rilind Gashi
 * @license MIT
 */

public class PoiDao extends DatabaseObjectAbstract {
    private Box<Poi> poiBox;
    private Query<Poi> poiQuery;
    private QueryBuilder<Poi> poiQueryBuilder;
    Property columnProperty;
    private static PoiService service;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public PoiDao(BoxStore boxStore, Context context) {
        poiBox = boxStore.boxFor(Poi.class);
        poiQueryBuilder = poiBox.query();

        if(service == null){
            service = ServiceGenerator.createService(PoiService.class);
        }
    }

    public long count() {
        return poiBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi_.class);
        poiQueryBuilder.equal(columnProperty, searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param poi (required).
     */
    /*
    public Poi update(final AbstractModel poi, final FragmentHandler handler) {
        PoiService service = ServiceGenerator.createService(PoiService.class);

        Call<Poi> call = service.createPoi(poi);
        @Override
        public void onResponse(Call<Poi> call, Response<Poi> response) {
            if(response.isSuccessful()) handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
            else handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
        }
        @Override
        public void onFailure(Call<Poi> call, Throwable t) {
            handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
        }

        return poi;
    }
*/
    @Override
    public void addImage(final File file, final int poiID){

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        Call<Poi> call = service.uploadImage(poiID, body);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {

            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {

            }
        });
    }
    /**
     * Insert an user into the database.
     *
     * @param poi (required).
     */
    public void create(final Poi poi) {
        PoiService service = ServiceGenerator.createService(PoiService.class);
        Call<Poi> call = service.createPoi(poi);
        call.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if(response.isSuccessful()) {
                    poiBox.put(poi);
                } else{

                }
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {

            }
        });
    }

    /**
     * Return a list with all poi
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
        Field searchedField = Poi_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi_.class);
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
        Field searchedField = Poi_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi_.class);
        poiQueryBuilder.equal(columnProperty, searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.find();
    }

    public void delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        poiBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteAll() {
        poiBox.removeAll();
    }

}
