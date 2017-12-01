package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultType;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * DifficultTypeDao
 * @author Rilind Gashi
 * @license MIT
 */

public class DifficultTypeDao extends DatabaseObjectAbstract{
    private Box<DifficultType> difficultTypeBox;
    private Query<DifficultType> difficultTypeQuery;
    private QueryBuilder<DifficultType> difficultTypeQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public DifficultTypeDao(BoxStore boxStore){
        difficultTypeBox = boxStore.boxFor(DifficultType.class);
        difficultTypeQueryBuilder = difficultTypeBox.query();
    }

    public long count(){
        return difficultTypeBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = DifficultType.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(DifficultType.class);
        difficultTypeQueryBuilder.equal(columnProperty , searchPattern);
        difficultTypeQuery = difficultTypeQueryBuilder.build();
        return difficultTypeQuery.find().size();
    }

    /**
     * Update an existing difficulty in the database.
     *
     * @param difficultType (required).
     *
     */
    public DifficultType update(DifficultType difficultType){
        difficultTypeBox.put(difficultType);
        return difficultType;
    }
    /**
     * Insert a difficulty type into the database.
     *
     * @param difficultType (required).
     *
     */
    public void create(DifficultType difficultType){
        difficultTypeBox.put(difficultType);
    }

    /**
     * Return a list with all difficulty types
     *
     * @return List<DifficultType>
     */
    public List<DifficultType> find() {
        return difficultTypeBox.getAll();
    }

    /**
     * Searching for a single difficulty type with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Difficulty type which match to the search pattern in the searched columns
     */
    public DifficultType findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = DifficultType.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(DifficultType.class);
        difficultTypeQueryBuilder.equal(columnProperty, searchPattern);
        difficultTypeQuery = difficultTypeQueryBuilder.build();
        return difficultTypeQuery.findFirst();
    }

    /**
     * Searching for difficulty type matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<DifficultType> which contains the difficulty types, which match to the search pattern in the searched columns
     */
    public List<DifficultType> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = DifficultType.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(DifficultType.class);
        difficultTypeQueryBuilder.equal(columnProperty , searchPattern);
        difficultTypeQuery = difficultTypeQueryBuilder.build();
        return difficultTypeQuery.find();
    }

    public void delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        DifficultType difficultType = findOne(searchedColumn, searchPattern);
        difficultTypeBox.remove(difficultType);
    }

    public void deleteAll(){
        difficultTypeBox.removeAll();
    }
}
