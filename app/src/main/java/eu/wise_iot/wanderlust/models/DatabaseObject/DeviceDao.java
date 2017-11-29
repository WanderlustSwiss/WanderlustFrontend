package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Device;
import eu.wise_iot.wanderlust.models.DatabaseModel.Device_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * DeviceDao
 * @author Rilind Gashi
 * @license MIT
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

    public long count(){
        return deviceBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Device_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Device_.class);
        deviceQueryBuilder.equal(columnProperty , searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param device (required).
     *
     */

    public Device update(final Device device,final FragmentHandler handler){
        deviceBox.put(device);
        return device;
    }

    /**
     * Insert a device into the database.
     *
     * @param device (required).
     *
     */
    public void create(Device device){
        deviceBox.put(device);
    }

    /**
     * Return a list with all devices
     *
     * @return List<Device>
     */
    public List<Device> find() {
        return deviceBox.getAll();
    }

    /**
     * Searching for a single device with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Device which match to the search pattern in the searched columns
     */
    public Device findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Device_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Device_.class);
        deviceQueryBuilder.equal(columnProperty, searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.findFirst();
    }

    /**
     * Searching for device matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Device> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Device> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Device_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Device_.class);
        deviceQueryBuilder.equal(columnProperty , searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.find();
    }

    public void delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        deviceBox.remove(findOne(searchedColumn, searchPattern));

    }

    public void deleteAll(){
        deviceBox.removeAll();
    }


}
