package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * TourDao
 * @author Rilind Gashi
 * @license MIT
 */


public class TourDao {
    private Box<Tour> routeBox;
    private Query<Tour> routeQuery;
    private QueryBuilder<Tour> routeQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public TourDao(BoxStore boxStore){
        routeBox = boxStore.boxFor(Tour.class);
        routeQueryBuilder = routeBox.query();
    }

    public long count(){
        return routeBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Tour_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Tour_.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param tour (required).
     *
     */
    public Tour update(Tour tour){
        routeBox.put(tour);
        return tour;
    }
    /**
     * Insert an history into the database.
     *
     * @param tour (required).
     *
     */
    public void create(Tour tour){
        routeBox.put(tour);
    }

    /**
     * Return a list with all routes
     *
     * @return List<Tour>
     */
    public List<Tour> find() {
        return routeBox.getAll();
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Tour which match to the search pattern in the searched columns
     */
    public Tour findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Tour_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Tour_.class);
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
     * @return List<Tour> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<Tour> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Tour_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Tour_.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find();
    }

    public Tour delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        routeBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        routeBox.removeAll();
    }


}
