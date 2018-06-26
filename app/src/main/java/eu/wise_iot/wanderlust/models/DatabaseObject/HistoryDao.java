package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.models.DatabaseModel.History;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;

/**
 * HistoryDao
 *
 * @author Rilind Gashi
 * @license GPL-3.0
 */
public class HistoryDao {
    private static class Holder {
        private static final HistoryDao INSTANCE = new HistoryDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static HistoryDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }
    private final Box<History> historyBox;

    /**
     * Constructor.
     */

    private HistoryDao() {
        historyBox = BOXSTORE.boxFor(History.class);
    }

    public long count() {
        return historyBox.count();
    }

    public long count(Property searchedColumn, String searchPattern) {
        return find(searchedColumn, searchPattern).size();
    }

    public long count(Property searchedColumn, long searchPattern) {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param history (required).
     */
    public History update(History history) {
        historyBox.put(history);
        return history;
    }

    /**
     * Insert an history into the database.
     *
     * @param history (required).
     */
    public void create(History history) {
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
     * @param searchPattern  (required) contain the search pattern.
     * @return History which match to the search pattern in the searched columns
     */
    @SuppressWarnings("WeakerAccess")
    public History findOne(Property searchedColumn, String searchPattern) {
        return historyBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public History findOne(Property searchedColumn, long searchPattern) {
        return historyBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for history matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<History> which contains the "done tours", which match to the search pattern in the searched columns
     */
    @SuppressWarnings("WeakerAccess")
    public List<History> find(Property searchedColumn, String searchPattern) {
        return historyBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    /**
     * Searching for history matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<History> which contains the "done tours", which match to the search pattern in the searched columns
     */
    @SuppressWarnings("WeakerAccess")
    public List<History> find(Property searchedColumn, long searchPattern) {
        return historyBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    /**
     * Searching for history matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<History> which contains the "done tours", which match to the search pattern in the searched columns
     */
    public List<History> find(Property searchedColumn, boolean searchPattern) {
        return historyBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        historyBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteAll() {
        historyBox.removeAll();
    }

}

