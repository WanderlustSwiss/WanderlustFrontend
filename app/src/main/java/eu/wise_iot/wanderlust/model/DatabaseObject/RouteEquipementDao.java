package eu.wise_iot.wanderlust.model.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.model.DatabaseModel.Device_;
import eu.wise_iot.wanderlust.model.DatabaseModel.RouteEquipement_;
import eu.wise_iot.wanderlust.model.DatabaseModel.RouteEquipement;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * RouteEquipementDao
 * @author Rilind Gashi
 * @license MIT
 */

public class RouteEquipementDao extends DatabaseObjectAbstract{

    private Box<RouteEquipement> deviceBox;
    private Query<RouteEquipement> deviceQuery;
    private QueryBuilder<RouteEquipement> deviceQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public RouteEquipementDao(BoxStore boxStore){
        deviceBox = boxStore.boxFor(RouteEquipement.class);
        deviceQueryBuilder = deviceBox.query();
    }

    public long count(){
        return deviceBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Device_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Device_.class);
        deviceQueryBuilder.equal(columnProperty , searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.find().size();
    }


    public RouteEquipement update(RouteEquipement device){
        return null;
    }

    /**
     * Insert an user into the database.
     *
     * @param routeEquipement (required).
     *
     */
    public void create(RouteEquipement routeEquipement){
        deviceBox.put(routeEquipement);
    }

    /**
     * Return a list with all device
     *
     * @return List<RouteEquipement>
     */
    public List<RouteEquipement> find() {
        return deviceBox.getAll();
    }

    /**
     * Searching for a single route equipement with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Route Equipement which match to the search pattern in the searched columns
     */
    public RouteEquipement findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = RouteEquipement_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteEquipement_.class);
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
     * @return List<RouteEquipement> which contains the route equipements, which match to the search pattern in the searched columns
     */
    public List<RouteEquipement> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = RouteEquipement_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteEquipement_.class);
        deviceQueryBuilder.equal(columnProperty , searchPattern);
        deviceQuery = deviceQueryBuilder.build();
        return deviceQuery.find();
    }

    public RouteEquipement delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        deviceBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        deviceBox.removeAll();
    }


}
