package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType;
import eu.wise_iot.wanderlust.services.DifficultyTypeService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DifficultyTypeDao extends DatabaseObjectAbstract {

    private static class Holder {
        private static final DifficultyTypeDao INSTANCE = new DifficultyTypeDao();
    }

    private static BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static DifficultyTypeDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private static DifficultyTypeService service;
    private Box<DifficultyType> difficultyTypeBox;

    /**
     * Constructor.
     */

    private DifficultyTypeDao() {
        difficultyTypeBox = BOXSTORE.boxFor(DifficultyType.class);
        service = ServiceGenerator.createService(DifficultyTypeService.class);
    }

    /**
     * Update all difficulty types in the database.
     */
    public void retrive() {
        Call<List<DifficultyType>> call = service.retrieveAllDifficultyTypes();
        call.enqueue(new Callback<List<DifficultyType>>() {
            @Override
            public void onResponse(Call<List<DifficultyType>> call, Response<List<DifficultyType>> response) {
                if (response.isSuccessful()) {
                    difficultyTypeBox.removeAll();
                    for (DifficultyType difficultyType : response.body()) {
                        difficultyTypeBox.put(difficultyType);
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
     * Searching for a single poi type with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return DifficultyType which match to the search pattern in the searched columns
     */
    public DifficultyType findOne(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return difficultyTypeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public DifficultyType findOne(Property searchedColumn, long searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return difficultyTypeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }
}
