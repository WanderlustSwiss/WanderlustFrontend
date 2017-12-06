package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.services.DifficultyTypeService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DifficultyTypeDao extends DatabaseObjectAbstract {

    private Box<DifficultyType> difficultyTypeBox;
    private Query<DifficultyType> difficultyTypeQuery;
    private QueryBuilder<DifficultyType> difficultyTypeQueryBuilder;
    private static DifficultyTypeService service;
    Property columnProperty;

    /**
     * Constructor.
     */

    public DifficultyTypeDao(){
        difficultyTypeBox = DatabaseController.boxStore.boxFor(DifficultyType.class);
        difficultyTypeQueryBuilder = difficultyTypeBox.query();
        if (service == null) service = ServiceGenerator.createService(DifficultyTypeService.class);
    }

    /**
     * Update all Poi types in the database.
     *
     */
    public void sync(){
        Call<List<DifficultyType>> call = service.retrieveAllDifficultyTypes();
        call.enqueue(new Callback<List<DifficultyType>>() {
             @Override
             public void onResponse(Call<List<DifficultyType>> call, Response<List<DifficultyType>> response) {
                if (response.isSuccessful()) {
                    for (DifficultyType poiType : response.body()){
                        difficultyTypeBox.put(poiType);
                    }
                }
             }

             @Override
             public void onFailure(Call<List<DifficultyType>> call, Throwable t) {

             }
         });
    }

    /**
     * Return a list with all devices
     *
     * @return List<DifficultyType>
     */
    public List<DifficultyType> find() {
        return difficultyTypeBox.getAll();
    }

    /**
     * Searching for a single poi t
     * ype with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return DifficultyType which match to the search pattern in the searched columns
     */
    public DifficultyType findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = PoiType.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(PoiType.class);
        difficultyTypeQueryBuilder.equal(columnProperty, searchPattern);
        difficultyTypeQuery = difficultyTypeQueryBuilder.build();
        return difficultyTypeQuery.findFirst();
    }
}
