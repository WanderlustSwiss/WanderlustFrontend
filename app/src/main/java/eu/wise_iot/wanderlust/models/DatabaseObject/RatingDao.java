package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.util.Log;

import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating_;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourRate;
import eu.wise_iot.wanderlust.services.RatingService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * RatingDao
 *
 * @author Rilind Gashi
 * @license GPL-3.0
 */
public class RatingDao extends DatabaseObjectAbstract{

    private static class Holder {
        private static final RatingDao INSTANCE = new RatingDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static RatingDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }
    private static final String TAG = "RatingDao";
    private final Box<Rating> RatingBox;
    private final RatingService service;

    private RatingDao(){
        RatingBox = BOXSTORE.boxFor(Rating.class);
        service = ServiceGenerator.createService(RatingService.class);
    }

    /**
     * Insert a Rating into the database
     *
     * //@param handler
     * @param tourRating
      * @param handler
     */
    public void create(Rating tourRating, final FragmentHandler handler) {
        Call<Rating> call = service.createRating(tourRating);
        call.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, retrofit2.Response<Rating> response) {
                if (response.isSuccessful()) {
                    Rating newRating = response.body();
                    tourRating.setInternal_id(0);
                    RatingBox.put(tourRating);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()),
                            newRating));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * delete a Rating in the database
     *
     * @param Rating_id
     * @param handler
     */
    public void delete(long Rating_id, final FragmentHandler handler) {
        Call<Rating> call = service.deleteRating(Rating_id);
        call.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful()) {
                    try {
                        Rating Rating = findOne(Rating_.rat_id, Rating_id);
                        if (Rating != null){
                            RatingBox.remove(Rating.getInternal_id());
                            handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                        }
                    } catch (Exception e){
                        if (BuildConfig.DEBUG) Log.d(TAG, e.getMessage());
                    }
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void retrieveTour(long tourId, final FragmentHandler handler) {
        Call<TourRate> call = service.retrieveTourRating(tourId);
        call.enqueue(new Callback<TourRate>() {
            @Override
            public void onResponse(Call<TourRate> call, Response<TourRate> response) {
                if (response.isSuccessful()) {
                    TourRate ratingStats = response.body();
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()),
                            ratingStats));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<TourRate> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void retrieve(long id, final FragmentHandler handler) {
        Call<Rating> call = service.retrieveRating(id);
        call.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                if (response.isSuccessful()) {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()),
                            response.body().getRate()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }


    public long count() {
        return RatingBox.count();
    }

    /**
     * Return a list with all Rating tours
     *
     * @return List<Rating>
     */
    public List<Rating> find() {
        return RatingBox.getAll();
    }

    /**
     * Searching for a single Rating tour with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Rating which match to the search pattern in the searched columns
     */
    @SuppressWarnings("WeakerAccess")
    public Rating findOne(Property searchedColumn, String searchPattern) {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    @SuppressWarnings("WeakerAccess")
    public Rating findOne(Property searchedColumn, long searchPattern) {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Rating findOne(Property searchedColumn, long searchPattern, long additionalSearchPattern) {
        return RatingBox.query().equal(searchedColumn, searchPattern)
                .equal(Rating_.user, additionalSearchPattern).build().findFirst();
    }

    /**
     * Searching for Rating tour matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Rating> which contains the equipments, which match to the search pattern in the searched columns
     */
    public List<Rating> find(Property searchedColumn, String searchPattern) {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Rating> find(Property searchedColumn, long searchPattern) {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Rating> find(Property searchedColumn, boolean searchPattern) {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        RatingBox.remove(findOne(searchedColumn, searchPattern));
    }


}

