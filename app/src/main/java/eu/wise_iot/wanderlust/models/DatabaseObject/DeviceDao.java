package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Device;
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
    Property columnProperty;

    /**
     * Constructor.
     */

    public DeviceDao(){
        deviceBox = DatabaseController.boxStore.boxFor(Device.class);
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

    public void deleteAll(){
        deviceBox.removeAll();
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
    public Device findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return deviceBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Device findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return deviceBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for device matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Device> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Device> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return deviceBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Device> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return deviceBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Device> find(Property searchedColumn, boolean searchPattern) {
        return deviceBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        deviceBox.remove(findOne(searchedColumn, searchPattern));
    }

    /**
     * count all poi
     *
     * @return Total number of records
     */
    public long count(){
        return deviceBox.count();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

}
