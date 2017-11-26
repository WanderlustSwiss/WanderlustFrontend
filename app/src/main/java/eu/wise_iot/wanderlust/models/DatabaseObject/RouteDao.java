package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Route;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * RouteDao
 * @author Rilind Gashi
 * @license MIT
 */


public class RouteDao {
    private Box<Route> routeBox;
    private Query<Route> routeQuery;
    private QueryBuilder<Route> routeQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public RouteDao(BoxStore boxStore){
        routeBox = boxStore.boxFor(Route.class);
        routeQueryBuilder = routeBox.query();
    }

    public long count(){
        return routeBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Route.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Route.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param route (required).
     *
     */
    public Route update(Route route){
        routeBox.put(route);
        return route;
    }
    /**
     * Insert an history into the database.
     *
     * @param route (required).
     *
     */
    public void create(Route route){
        routeBox.put(route);
    }

    /**
     * Return a list with all routes
     *
     * @return List<Route>
     */
    public List<Route> find() {
        return routeBox.getAll();
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Route which match to the search pattern in the searched columns
     */
    public Route findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Route.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Route.class);
        routeQueryBuilder.equal(columnProperty, searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.findFirst();
    }

    /**
     * Searching for routes matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Route> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<Route> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Route.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Route.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find();
    }

    public Route delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        routeBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        routeBox.removeAll();
    }


}
