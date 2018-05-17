package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;

import static android.util.Log.d;
import static eu.wise_iot.wanderlust.controllers.EventType.NOT_FOUND;
import static eu.wise_iot.wanderlust.controllers.EventType.OK;

/**
 * RecentTourDao:
 *
 * represents interface for all calls on recent tours
 * @author Alexander Weinbeck
 * @license MIT
 */

public class RecentTourDao extends DatabaseObjectAbstract {


    private static class Holder {
        private static final RecentTourDao INSTANCE = new RecentTourDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static RecentTourDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }
    private final Box<Tour> recentTourBox;

    public final UserTourDao userTourDao = UserTourDao.getInstance();


    private RecentTourDao() {
        recentTourBox = BOXSTORE.boxFor(Tour.class);
    }

    /**
     * get all recent Tours
     * @return
     */
    public List<Tour> retrieveAll() {
        return recentTourBox.getAll();
    }
    /**
     * count all poi
     *
     * @return Total number of records
     */
    public long count() {
        return recentTourBox.count();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, String searchPattern) {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern){
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing recentTour in the database.
     *
     * @param recentTour (required).
     */
    public Tour update(Tour recentTour) {
        recentTourBox.put(recentTour);
        return recentTour;
    }

    /**
     * update the given recent tours on startup
     */
    public void updateRecentToursOnStartup() {
        try {
            for(Tour tour : this.find()) {
                new Thread(() -> {
                    try {
                        userTourDao.retrieve(tour.getTour_id(), controllerEvent -> {
                            switch (controllerEvent.getType()) {
                                case OK:
                                    d("RECENTTOUR UPDATE","OK");
                                    break;
                                case NOT_FOUND:
                                    d("RECENTTOUR UPDATE","NOT FOUND");
                                    this.remove(tour);
                                    break;
                                default:
                            }
                        });
                    } catch (Exception e){
                        d("RECENTTOUR UPDATE","EXCEPTION THROWN IN THREAD");
                    }
                }).start();
            }
        } catch(Exception e){
            d("FAILURE","EXCEPTION THROWN IN METHOD");
        }
    }
    /**
     * Remove an existing recentTour in the database.
     *
     * @param recentTour (required).
     */
    public void remove(Tour recentTour) {
        recentTourBox.remove(recentTour);
    }

    /**
     * insert a recentTour local and remote
     *
     * @param tour
     */
    public void create(final Tour tour) {
        if(findOne(Tour_.tour_id, tour.getTour_id()) != null) return;
        if(recentTourBox.count() > 5) recentTourBox.remove(recentTourBox.count() - 1);
        recentTourBox.put(tour);
    }

    /**
     * get recentTour with specific id
     *
     * @param id
     */
    public Tour retrieve(int id) {
        return recentTourBox.get(id);
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Tour which match to the search pattern in the searched columns
     */
    public Tour findOne(Property searchedColumn, String searchPattern) {
        return recentTourBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Tour findOne(Property searchedColumn, long searchPattern) {
        return recentTourBox.query().equal(searchedColumn, searchPattern).build().findFirst();
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
        return recentTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find(Property searchedColumn, long searchPattern) {
        return recentTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find(Property searchedColumn, boolean searchPattern) {
        return recentTourBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Tour> find() {
        if (recentTourBox != null)
            return recentTourBox.getAll();
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
    public void deleteByPattern(Property searchedColumn, String searchPattern) {
        recentTourBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void delete(Tour recentTour){
        recentTourBox.remove(recentTour);
    }
}
