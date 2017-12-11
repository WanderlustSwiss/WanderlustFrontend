package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.CommunityTour;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * CommunityTourDao:
 *
 * @author Rilind Gashi, Alexander Weinbeck
 * @license MIT
 */


public class CommunityTourDao extends DatabaseObjectAbstract {
    private Box<CommunityTour> communityTourBox;
    Property columnProperty;

    /**
     * Constructor.
     */

    public CommunityTourDao(){
        communityTourBox = DatabaseController.boxStore.boxFor(CommunityTour.class);
    }

    /**
     * count all poi
     *
     * @return Total number of records
     */
    public long count(){
        return communityTourBox.count();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing communityTour in the database.
     *
     * @param communityTour (required).
     *
     */
    public CommunityTour update(CommunityTour communityTour){
        communityTourBox.put(communityTour);
        return communityTour;
    }

    /**
     * insert a communityTour local and remote
     * @param communityTour
     * @param handler
     */
    public void create(final AbstractModel communityTour, final FragmentHandler handler){
        communityTourBox.put((CommunityTour)communityTour);
    }
    /**
     * get communityTour with specific id
     * @param id
     */
    public CommunityTour retrieve(int id){
        return communityTourBox.get(id);
    }
    /**
     * get all communityTours out of the remote database
     */
    public List<CommunityTour> retrieveAll(){
        return communityTourBox.getAll();
    }
    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return CommunityTour which match to the search pattern in the searched columns
     */
    public CommunityTour findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public CommunityTour findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for routes matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<CommunityTour> which contains the equipements,
     * which match to the search pattern in the searched columns
     */
    public List<CommunityTour> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<CommunityTour> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<CommunityTour> find(Property searchedColumn, boolean searchPattern) {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    /**
     * delete:
     * Deleting a CommunityTour which matches the given pattern
     * @param searchedColumn
     * @param searchPattern
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void deleteByPattern(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        communityTourBox.remove(findOne(searchedColumn, searchPattern));
    }
}
