package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourKit;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.TourKitService;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * TourKitDao
 *
 * @author Rilind Gashi
 * @license MIT
 */

public class TourKitDao extends DatabaseObjectAbstract {

    private static TourKitService service;


    private static class Holder {
        private static final TourKitDao INSTANCE = new TourKitDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static TourKitDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private final Box<TourKit> tourKitBox;

    /**
     * Constructor.
     */

    private TourKitDao() {
        service = ServiceGenerator.createService(TourKitService.class);
        tourKitBox = BOXSTORE.boxFor(TourKit.class);
    }

    public long count() {
        return tourKitBox.count();
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

    public TourKit update(TourKit tourKit) {
        return null;
    }

    /**
     * Insert an user into the database.
     *
     * @param tourKit (required).
     */
    public void create(TourKit tourKit, FragmentHandler handler) {

        Call<TourKit> call = service.addEquipmentToTour(tourKit);
        call.enqueue(new Callback<TourKit>() {
            @Override
            public void onResponse(Call<TourKit> call, retrofit2.Response<TourKit> response) {
                if (response.isSuccessful()) {
                    TourKit tourKit = response.body();
                    tourKitBox.put(tourKit);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), tourKit));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<TourKit> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Return a list with all tourEquipement
     *
     * @return List<TourKit>
     */
    public List<TourKit> find() {
        return tourKitBox.getAll();
    }

    /**
     * Searching for a single tour equipement with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return CommunityTours Equipment which match to the search pattern in the searched columns
     */
    public TourKit findOne(Property searchedColumn, String searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public TourKit findOne(Property searchedColumn, long searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<TourKit> which contains the tour equipements, which match to the search pattern in the searched columns
     */
    public List<TourKit> find(Property searchedColumn, String searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<TourKit> find(Property searchedColumn, long searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<TourKit> find(Property searchedColumn, boolean searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        tourKitBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteAll() {
        tourKitBox.removeAll();
    }


}
