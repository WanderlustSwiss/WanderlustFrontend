package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseObject.DatabaseObjectAbstract;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * PoiDao
 * @author Rilind Gashi
 * @license MIT
 */

public class PoiDao extends DatabaseObjectAbstract{
    private Box<Poi> poiBox;
    private Query<Poi> poiQuery;
    private QueryBuilder<Poi> poiQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public PoiDao(BoxStore boxStore){
        poiBox = boxStore.boxFor(Poi.class);
        poiQueryBuilder = poiBox.query();
    }

    public long count(){
        return poiBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi.class);
        poiQueryBuilder.equal(columnProperty , searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param poi (required).
     *
     */
    public Poi update(Poi poi){
        poiBox.put(poi);
        return poi;
    }
    /**
     * Insert an user into the database.
     *
     * @param poi (required).
     *
     */
    public void create(Poi poi){
        poiBox.put(poi);
    }

    /**
     * Return a list with all poi
     *
     * @return List<Poi>
     */
    public List<Poi> find() {
        return poiBox.getAll();
    }

    /**
     * Searching for a single user with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return User who match to the search pattern in the searched columns
     */
    public Poi findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi.class);
        poiQueryBuilder.equal(columnProperty, searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Poi> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Poi> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Poi.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Poi.class);
        poiQueryBuilder.equal(columnProperty , searchPattern);
        poiQuery = poiQueryBuilder.build();
        return poiQuery.find();
    }

    public Poi delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        poiBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        poiBox.removeAll();
    }

}
