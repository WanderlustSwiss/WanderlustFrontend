package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PoiTypeDao extends DatabaseObjectAbstract {

    private static class Holder {
        private static final PoiTypeDao INSTANCE = new PoiTypeDao();
    }

    private static BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static PoiTypeDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private static PoiService service;
    private Box<PoiType> poiTypeBox;

    /**
     * Constructor.
     */

    private PoiTypeDao() {
        poiTypeBox = BOXSTORE.boxFor(PoiType.class);
        service = ServiceGenerator.createService(PoiService.class);
    }

    /**
     * Update all Poi types in the database.
     */
    public void syncTypes() {
        Call<List<PoiType>> call = service.retrieveAllPoiTypes();
        call.enqueue(new Callback<List<PoiType>>() {
            @Override
            public void onResponse(Call<List<PoiType>> call, Response<List<PoiType>> response) {
                if (response.isSuccessful()) {
                    poiTypeBox.removeAll();
                    poiTypeBox.put(response.body());
                }

                DatabaseController.getInstance().syncPoiTypesDone();
            }

            @Override
            public void onFailure(Call<List<PoiType>> call, Throwable t) {
                DatabaseController.getInstance().syncPoiTypesDone();
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
     * Searching for a single poi
     * type with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return PoiType which match to the search pattern in the searched columns
     */
    public PoiType findOne(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return poiTypeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public PoiType findOne(Property searchedColumn, long searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return poiTypeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }
}
