package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.models.DatabaseModel.History;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * HistoryDao
 * @author Rilind Gashi
 * @license MIT
 */

public class HistoryDao {

    private Box<History> historyBox;
    private Query<History> historyQuery;
    private QueryBuilder<History> historyQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     */

    public HistoryDao(){
        historyBox = DatabaseController.boxStore.boxFor(History.class);
        historyQueryBuilder = historyBox.query();
    }

    public long count(){
        return historyBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = History.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(History.class);
        historyQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        historyQuery = historyQueryBuilder.build();
        return historyQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param history (required).
     *
     */
    public History update(History history){
        historyBox.put(history);
        return history;
    }
    /**
     * Insert an history into the database.
     *
     * @param history (required).
     *
     */
    public void create(History history){
        historyBox.put(history);
    }

    /**
     * Return a list with all history
     *
     * @return List<History>
     */
    public List<History> find() {
        return historyBox.getAll();
    }

    /**
     * Searching for a single history with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return History which match to the search pattern in the searched columns
     */
    public History findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = History.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(History.class);
        historyQueryBuilder.equal(columnProperty, Integer.valueOf(searchPattern));
        historyQuery = historyQueryBuilder.build();
        return historyQuery.findFirst();
    }

    /**
     * Searching for history matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<History> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<History> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = History.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(History.class);
        historyQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        historyQuery = historyQueryBuilder.build();
        return historyQuery.find();
    }

    public History delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        historyBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        historyBox.removeAll();
    }

}

