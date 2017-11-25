package eu.wise_iot.wanderlust.model.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.model.DatabaseModel.History_;
import eu.wise_iot.wanderlust.model.DatabaseModel.History;
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
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public HistoryDao(BoxStore boxStore){
        historyBox = boxStore.boxFor(History.class);
        historyQueryBuilder = historyBox.query();
    }

    public int count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return 0;
    }

    public History update(History history){
        return null;
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
        Field searchedField = History_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(History_.class);
        historyQueryBuilder.equal(columnProperty, searchPattern);
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
        Field searchedField = History_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(History_.class);
        historyQueryBuilder.equal(columnProperty , searchPattern);
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

