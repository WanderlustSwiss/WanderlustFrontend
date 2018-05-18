package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType;
import eu.wise_iot.wanderlust.models.DatabaseModel.ViolationType_;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.ViolationTypeService;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViolationTypeDao
 *
 * @author Alexander Weinbeck
 * @license MIT
 */

public class ViolationTypeDao extends DatabaseObjectAbstract{

    private static class Holder {
        private static final ViolationTypeDao INSTANCE = new ViolationTypeDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();
    private static final String TAG = "ViolationTypeDao";

    public static ViolationTypeDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private final Box<ViolationType> violationTypeBox;
    private final ViolationTypeService service;
    private ViolationTypeDao(){
        violationTypeBox = BOXSTORE.boxFor(ViolationType.class);
        service = ServiceGenerator.createService(ViolationTypeService.class);
    }

    /**
     * get the violationType by name
     * @param name
     * @return
     */
    public ViolationType getViolationTypebyName(String name){

        return this.findOne(ViolationType_.name,name);
    }

    /**
     * Update all difficulty types in the database.
     */
    public void retrieveAllViolationTypes() {
        Call<List<ViolationType>> call = service.retrieveAllViolationTypes();
        call.enqueue(new Callback<List<ViolationType>>() {
            @Override
            public void onResponse(Call<List<ViolationType>> call, Response<List<ViolationType>> response) {
                if (response.isSuccessful()) {
                    violationTypeBox.removeAll();
                    for (ViolationType violationType : response.body()) {
                        violationTypeBox.put(violationType);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<ViolationType>> call, Throwable t) {
            }
        });
    }


    public long count() {
        return violationTypeBox.count();
    }

    /**
     * Return a list with all ViolationType tours
     *
     * @return List<ViolationType>
     */
    public List<ViolationType> find() {
        return violationTypeBox.getAll();
    }

    /**
     * Searching for a single ViolationType tour with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return ViolationType which matches the search pattern in the searched columns
     */
    @SuppressWarnings("WeakerAccess")
    public ViolationType findOne(Property searchedColumn, String searchPattern) {
        return violationTypeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public ViolationType findOne(Property searchedColumn, long searchPattern) {
        return violationTypeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for ViolationType tour matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<ViolationType> which contains the equipments, which match to the search pattern in the searched columns
     */
    public List<ViolationType> find(Property searchedColumn, String searchPattern) {
        return violationTypeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<ViolationType> find(Property searchedColumn, long searchPattern) {
        return violationTypeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<ViolationType> find(Property searchedColumn, boolean searchPattern) {
        return violationTypeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        violationTypeBox.remove(findOne(searchedColumn, searchPattern));
    }


}

