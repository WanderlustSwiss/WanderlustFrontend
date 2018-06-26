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
 * @license GPL-3.0
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

    public long count(Property searchedColumn, String searchPattern) {
        return find(searchedColumn, searchPattern).size();
    }

    public long count(Property searchedColumn, long searchPattern) {
        return find(searchedColumn, searchPattern).size();
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


    public List<TourKit> find() {
        return tourKitBox.getAll();
    }

    public TourKit findOne(Property searchedColumn, String searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public TourKit findOne(Property searchedColumn, long searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public List<TourKit> find(Property searchedColumn, String searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<TourKit> find(Property searchedColumn, long searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<TourKit> find(Property searchedColumn, boolean searchPattern) {
        return tourKitBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        tourKitBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteAll() {
        tourKitBox.removeAll();
    }

}
