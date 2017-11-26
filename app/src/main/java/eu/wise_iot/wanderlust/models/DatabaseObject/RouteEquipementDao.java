package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.RouteEquipement;
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

    private Box<RouteEquipement> routeEquipementBox;
    private Query<RouteEquipement> routeEquipementQuery;
    private QueryBuilder<RouteEquipement> routeEquipementQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public RouteEquipementDao(BoxStore boxStore){
        routeEquipementBox = boxStore.boxFor(RouteEquipement.class);
        routeEquipementQueryBuilder = routeEquipementBox.query();
    }

    public long count(){
        return routeEquipementBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = RouteEquipement.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteEquipement.class);
        routeEquipementQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        routeEquipementQuery = routeEquipementQueryBuilder.build();
        return routeEquipementQuery.find().size();
    }


    public RouteEquipement update(RouteEquipement routeEquipement){
        return null;
    }

    /**
     * Insert an user into the database.
     *
     * @param routeEquipement (required).
     *
     */
    public void create(RouteEquipement routeEquipement){
        routeEquipementBox.put(routeEquipement);
    }

    /**
     * Return a list with all routeEquipement
     *
     * @return List<RouteEquipement>
     */
    public List<RouteEquipement> find() {
        return routeEquipementBox.getAll();
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
        Field searchedField = RouteEquipement.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteEquipement.class);
        routeEquipementQueryBuilder.equal(columnProperty, Integer.valueOf(searchPattern));
        routeEquipementQuery = routeEquipementQueryBuilder.build();
        return routeEquipementQuery.findFirst();
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
        Field searchedField = RouteEquipement.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteEquipement.class);
        routeEquipementQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        routeEquipementQuery = routeEquipementQueryBuilder.build();
        return routeEquipementQuery.find();
    }

    public RouteEquipement delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        routeEquipementBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        routeEquipementBox.removeAll();
    }


}
