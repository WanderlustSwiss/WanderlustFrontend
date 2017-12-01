package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoiTypeDao extends DatabaseObjectAbstract {
    
    private Box<PoiType> poiTypeBox;
    private Query<PoiType> poiTypeQuery;
    private QueryBuilder<PoiType> poiTypeQueryBuilder;
    private static PoiService service;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public PoiTypeDao(BoxStore boxStore){
        poiTypeBox = boxStore.boxFor(PoiType.class);
        poiTypeQueryBuilder = poiTypeBox.query();
        if (service == null) service = ServiceGenerator.createService(PoiService.class);
    }

    /**
     * Update all Poit types in the database.
     *
     */
    public void sync(){
        Call<List<PoiType>> call = service.retrieveAllPoiTypes();
        call.enqueue(new Callback<List<PoiType>>() {
             @Override
             public void onResponse(Call<List<PoiType>> call, Response<List<PoiType>> response) {
                if (response.isSuccessful()) {
                    for (PoiType poiType : response.body()){
                        poiTypeBox.put(poiType);
                    }
                }
             }

             @Override
             public void onFailure(Call<List<PoiType>> call, Throwable t) {

             }
         });
    }

    /**
     * Return a list with all devices
     *
     * @return List<PoiType>
     */
    public List<PoiType> find() {
        return poiTypeBox.getAll();
    }

    /**
     * Searching for a single poi t
     * ype with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return PoiType which match to the search pattern in the searched columns
     */
    public PoiType findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = PoiType.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(PoiType.class);
        poiTypeQueryBuilder.equal(columnProperty, searchPattern);
        poiTypeQuery = poiTypeQueryBuilder.build();
        return poiTypeQuery.findFirst();
    }
}
