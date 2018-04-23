package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;

/**
 * TourDao:
 *
 * @author Rilind Gashi, Alexander Weinbeck, Simon Kaspar
 * @license MIT
 */


public class CommunityTourDao extends DatabaseObjectAbstract {
    private static class Holder {
        private static final CommunityTourDao INSTANCE = new CommunityTourDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static CommunityTourDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }
    private final Box<Tour> communityTourBox;

    /**
     * Constructor.
     */

    private CommunityTourDao() {
        communityTourBox = BOXSTORE.boxFor(Tour.class);
    }

    /**
     * count all poi
     *
     * @return Total number of records
     */
    public long count() {
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
     */
    public Tour update(Tour communityTour) {
        communityTourBox.put(communityTour);
        return communityTour;
    }

    /**
     * insert a communityTour local and remote
     *
     * @param communityTour
     * @param handler
     */
    public void create(final AbstractModel communityTour, final FragmentHandler handler) {
        communityTourBox.put((Tour) communityTour);
    }

    /**
     * insert community tour only local
     *
     * @param communityTour
     */
    public void create(final AbstractModel communityTour){
        communityTourBox.put((Tour) communityTour);
    }

    /**
     * get communityTour with specific id
     *
     * @param id
     */
    public Tour retrieve(int id) {
        return communityTourBox.get(id);
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Tour which match to the search pattern in the searched columns
     */
    public Tour findOne(Property searchedColumn, String searchPattern) {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Tour findOne(Property searchedColumn, long searchPattern) {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for routes matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Tour> which contains the equipements,
     * which match to the search pattern in the searched columns
     */
    public List<Tour> find(Property searchedColumn, String searchPattern) {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find(Property searchedColumn, long searchPattern) {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find(Property searchedColumn, boolean searchPattern) {
        return communityTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find() {
        if (communityTourBox != null)
            return communityTourBox.getAll();
        else
            return null;
    }
    /**
     * delete:
     * Deleting a Tour which matches the given pattern
     *
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

    public void delete(Tour communityTour){
        List<Tour> list = communityTourBox.getAll();
        for(Tour t : list){
            if(t.getTour_id() == communityTour.getTour_id()){
                communityTourBox.remove(t);
            }
        }
    }

}
