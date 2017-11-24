package eu.wise_iot.wanderlust.model.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.model.DatabaseModel.Device;
import eu.wise_iot.wanderlust.model.DatabaseModel.Device_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * Created by rilindgashi on 24.11.17.
 */

public class DeviceDao extends DatabaseObjectAbstract{

    private Box<Device> deviceBox;
    private Query<Device> deviceQuery;
    private QueryBuilder<Device> deviceQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public DeviceDao(BoxStore boxStore){
        deviceBox = boxStore.boxFor(Device.class);
        deviceQueryBuilder = deviceBox.query();
    }

    public int count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return 0;
    }

    public Device update(Device device){
        return null;
    }
    /**
     * Insert an user into the database.
     *
     * @param user (required).
     *
     */
    public void create(Device device){
        deviceBox.put(device);
    }

    /**
     * Return a list with all user
     *
     * @return List<User>
     */
    public List<Device> find() {
        return deviceBox.getAll();
    }

    /**
     * Searching for a single user with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return User who match to the search pattern in the searched columns
     */
    public Device findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Device_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Device_.class);
        deviceQueryBuilder.equal(columnProperty, searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Device> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Device> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Device_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Device_.class);
        deviceQueryBuilder.equal(columnProperty , searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.find();
    }

    public Device delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        deviceBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        deviceBox.removeAll();
    }


}
