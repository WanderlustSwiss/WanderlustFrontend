package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType;
import eu.wise_iot.wanderlust.models.DatabaseModel.PoiType_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

public class PoiTypeDao extends DatabaseObjectAbstract {
    
    private Box<PoiType> deviceBox;
    private Query<PoiType> deviceQuery;
    private QueryBuilder<PoiType> deviceQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public PoiTypeDao(BoxStore boxStore){
        deviceBox = boxStore.boxFor(PoiType.class);
        deviceQueryBuilder = deviceBox.query();
    }

    public long count(){
        return deviceBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = PoiType_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(PoiType_.class);
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
    public PoiType update(PoiType device){
        deviceBox.put(device);
        return device;
    }

    /**
     * Insert a device into the database.
     *
     * @param device (required).
     *
     */
    public void create(PoiType device){
        deviceBox.put(device);
    }

    /**
     * Return a list with all devices
     *
     * @return List<PoiType>
     */
    public List<PoiType> find() {
        return deviceBox.getAll();
    }

    /**
     * Searching for a single device with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return PoiType which match to the search pattern in the searched columns
     */
    public PoiType findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = PoiType_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(PoiType_.class);
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
     * @return List<PoiType> which contains the users, who match to the search pattern in the searched columns
     */
    public List<PoiType> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = PoiType_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(PoiType_.class);
        deviceQueryBuilder.equal(columnProperty , searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.find();
    }

    public PoiType delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        deviceBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        deviceBox.removeAll();
    }



}
