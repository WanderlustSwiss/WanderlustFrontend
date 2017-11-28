package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment;
import eu.wise_iot.wanderlust.models.DatabaseModel.Equipment_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * EquipmentDao
 * @author Rilind Gashi
 * @license MIT
 */

public class EquipmentDao extends DatabaseObjectAbstract {
    private Box<Equipment> equipmentBox;
    private Query<Equipment> equipmentQuery;
    private QueryBuilder<Equipment> equipmentQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public EquipmentDao(BoxStore boxStore){
        equipmentBox = boxStore.boxFor(Equipment.class);
        equipmentQueryBuilder = equipmentBox.query();
    }

    public long count(){
        return equipmentBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Equipment_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Equipment_.class);
        equipmentQueryBuilder.equal(columnProperty , searchPattern);
        equipmentQuery = equipmentQueryBuilder.build();
        return equipmentQuery.find().size();
    }

    /**
     * Update an existing difficulty in the database.
     *
     * @param equipment (required).
     *
     */
    public Equipment update(Equipment equipment){
        equipmentBox.put(equipment);
        return equipment;
    }
    /**
     * Insert an equipment into the database.
     *
     * @param equipment (required).
     *
     */
    public void create(Equipment equipment){
        equipmentBox.put(equipment);
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
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Equipment which match to the search pattern in the searched columns
     */
    public Equipment findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Equipment_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Equipment_.class);
        equipmentQueryBuilder.equal(columnProperty, searchPattern);
        equipmentQuery = equipmentQueryBuilder.build();
        return equipmentQuery.findFirst();
    }

    /**
     * Searching for equipment matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Equipment> which contains the equipments, which match to the search pattern in the searched columns
     */
    public List<Equipment> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Equipment_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Equipment_.class);
        equipmentQueryBuilder.equal(columnProperty , searchPattern);
        equipmentQuery = equipmentQueryBuilder.build();
        return equipmentQuery.find();
    }

    public Equipment delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        equipmentBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        equipmentBox.removeAll();
    }

}
