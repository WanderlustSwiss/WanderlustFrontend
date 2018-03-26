package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating;
import eu.wise_iot.wanderlust.models.DatabaseModel.RatingAVG;
import eu.wise_iot.wanderlust.models.DatabaseModel.Rating_;
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
        Call<RatingAVG> call = service.retrieveRating(id);
        call.enqueue(new Callback<RatingAVG>() {
            @Override
            public void onResponse(Call<RatingAVG> call, Response<RatingAVG> response) {
                if (response.isSuccessful()) {
                    RatingAVG ratingAVG = response.body();
                    //RatingBox.put(backendRating);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()),
                            ratingAVG.getRateAvg()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<RatingAVG> call, Throwable t) {
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

    public Rating findOne(Property searchedColumn, long searchPattern, long additionalSearchPattern)
            throws NoSuchFieldException, IllegalAccessException {
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

