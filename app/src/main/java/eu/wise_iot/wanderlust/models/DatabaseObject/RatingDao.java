package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating_;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
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
 * @license MIT
 */

public class RatingDao extends DatabaseObjectAbstract{

    private static class Holder {
        private static final RatingDao INSTANCE = new RatingDao();
    }

    private static BoxStore BOXSTORE = DatabaseController.getBoxStore();

    public static RatingDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private Box<Rating> RatingBox;
    private RatingService service;

    private RatingDao(){
        RatingBox = BOXSTORE.boxFor(Rating.class);
        service = ServiceGenerator.createService(RatingService.class);
    }

    /**
     * Insert a Rating into the database
     *
     * //@param handler
     */
    public void create(Tour tour, final FragmentHandler handler) {
        Call<Rating> call = service.createRating(tour);
        call.enqueue(new Callback<Rating>() {
            @Override
            public void onResponse(Call<Rating> call, retrofit2.Response<Rating> response) {
                if (response.isSuccessful()) {
                    Rating newRating = response.body();
                    newRating.setInternal_id(0);
                    RatingBox.put(newRating);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), newRating));
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
     * Retriev all Rating tours
     *
     * @param handler
     */
    public void retrievAllRatingTours(final FragmentHandler handler) {
        Call<List<Tour>> call = service.retrievAllRatingTours();
        call.enqueue(new Callback<List<Tour>>() {
            @Override
            public void onResponse(Call<List<Tour>> call, retrofit2.Response<List<Tour>> response) {
                if (response.isSuccessful()) {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }
            @Override
            public void onFailure(Call<List<Tour>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }
    /**
     * Insert a Rating into the database
     *
     * @param handler
     */
    public void retrievAllRatings(final FragmentHandler handler) {
        Call<List<Rating>> call = service.retrievAllRatings();
        call.enqueue(new Callback<List<Rating>>() {
            @Override
            public void onResponse(Call<List<Rating>> call, retrofit2.Response<List<Rating>> response) {
                if (response.isSuccessful()) {
                    //write to local db
                    RatingBox.removeAll();
                    for(Rating Rating : response.body()){
                        Rating.setInternal_id(0);
                        RatingBox.put(Rating);
                    }
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }
            @Override
            public void onFailure(Call<List<Rating>> call, Throwable t) {
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
                    } catch (Exception e){}
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {
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
                    Rating backendRating = response.body();
                    RatingBox.put(backendRating);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendRating));
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
    public Rating findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Rating findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for Rating tour matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Rating> which contains the equipments, which match to the search pattern in the searched columns
     */
    public List<Rating> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Rating> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Rating> find(Property searchedColumn, boolean searchPattern) {
        return RatingBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        RatingBox.remove(findOne(searchedColumn, searchPattern));
    }


}

