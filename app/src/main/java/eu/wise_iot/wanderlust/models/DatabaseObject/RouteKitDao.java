package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.RouteKit_;
import eu.wise_iot.wanderlust.models.DatabaseModel.RouteKit;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * RouteKitDao
 * @author Rilind Gashi
 * @license MIT
 */

public class RouteKitDao extends DatabaseObjectAbstract{

    private Box<RouteKit> routeEquipementBox;
    private Query<RouteKit> routeEquipementQuery;
    private QueryBuilder<RouteKit> routeEquipementQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public RouteKitDao(BoxStore boxStore){
        routeEquipementBox = boxStore.boxFor(RouteKit.class);
        routeEquipementQueryBuilder = routeEquipementBox.query();
    }

    public long count(){
        return routeEquipementBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = RouteKit_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteKit_.class);
        routeEquipementQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        routeEquipementQuery = routeEquipementQueryBuilder.build();
        return routeEquipementQuery.find().size();
    }


    public RouteKit update(RouteKit routeKit){
        return null;
    }

    /**
     * Insert an user into the database.
     *
     * @param routeKit (required).
     *
     */
    public void create(RouteKit routeKit){
        routeEquipementBox.put(routeKit);
    }

    /**
     * Return a list with all routeEquipement
     *
     * @return List<RouteKit>
     */
    public List<RouteKit> find() {
        return routeEquipementBox.getAll();
    }

    /**
     * Searching for a single route equipement with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Route Equipment which match to the search pattern in the searched columns
     */
    public RouteKit findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = RouteKit_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteKit_.class);
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
     * @return List<RouteKit> which contains the route equipements, which match to the search pattern in the searched columns
     */
    public List<RouteKit> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = RouteKit_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(RouteKit_.class);
        routeEquipementQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        routeEquipementQuery = routeEquipementQueryBuilder.build();
        return routeEquipementQuery.find();
    }

    public RouteKit delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        routeEquipementBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        routeEquipementBox.removeAll();
    }


}
