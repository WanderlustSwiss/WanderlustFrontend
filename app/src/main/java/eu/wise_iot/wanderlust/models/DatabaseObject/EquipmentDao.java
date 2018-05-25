package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.services.EquipmentService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * EquipmentDao
 *
 * @author Rilind Gashi
 * @license MIT
 */

public class EquipmentDao extends DatabaseObjectAbstract {
    private static class Holder {
        private static final EquipmentDao INSTANCE = new EquipmentDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static EquipmentDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private final Box<Equipment> equipmentBox;
    private static EquipmentService service;


    /**
     * Constructor.
     */
    private EquipmentDao() {
        equipmentBox = BOXSTORE.boxFor(Equipment.class);
        service = ServiceGenerator.createService(EquipmentService.class);
    }


    public void retrieveEquipment(FragmentHandler handler){
        Call<List<Equipment>> call = service.getEquipment();
        call.enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful()) {
                    equipmentBox.removeAll();
                    equipmentBox.put(response.body());
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Return a list with all equipment
     *
     * @return List<Equipment>
     */
    public List<Equipment> find() {
        return equipmentBox.getAll();
    }

    /**
     * Searching for a single equipment with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Equipment which match to the search pattern in the searched columns
     */
    public Equipment findOne(Property searchedColumn, String searchPattern) {
        return equipmentBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Equipment findOne(Property searchedColumn, long searchPattern) {
        return equipmentBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for equipment matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Equipment> which contains the equipments, which match to the search pattern in the searched columns
     */
    public List<Equipment> find(Property searchedColumn, String searchPattern) {
        return equipmentBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Equipment> find(Property searchedColumn, long searchPattern) {
        return equipmentBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Equipment> find(Property searchedColumn, boolean searchPattern) {
        return equipmentBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void deleteAll() {
        equipmentBox.removeAll();
    }

}
