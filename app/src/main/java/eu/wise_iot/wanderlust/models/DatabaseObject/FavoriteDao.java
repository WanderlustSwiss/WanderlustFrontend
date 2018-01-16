package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.DatabaseEvent;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.services.FavoriteService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * FavoriteDao
 *
 * @author Rilind Gashi
 * @license MIT
 */

public class FavoriteDao extends DatabaseObjectAbstract{

    private Box<Favorite> favoriteBox;
    private Query<Favorite> favoriteQuery;
    private QueryBuilder<Favorite> favoriteQueryBuilder;
    private FavoriteService service;

    public FavoriteDao(){
        this.favoriteBox = DatabaseController.boxStore.boxFor(Favorite.class);
        service = ServiceGenerator.createService(FavoriteService.class);
    }

    /**
     * Insert a favorite into the database
     *
     * @param favorite     (required)
     * //@param handler
     */
    public void create(final Favorite favorite, final FragmentHandler handler) {
        Call<Favorite> call = service.createFavorite(favorite);
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, retrofit2.Response<Favorite> response) {
                if (response.isSuccessful()) {
                    Favorite newFavorite = response.body();
                    newFavorite.setInternal_id(0);
                    favoriteBox.put(newFavorite);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), newFavorite));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Insert a favorite into the database
     *
     * @param handler
     */
    public void retrievAllFavorites(final FragmentHandler handler) {
        Call<List<Favorite>> call = service.retrievAllFavorites();
        call.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, retrofit2.Response<List<Favorite>> response) {
                if (response.isSuccessful()) {
                    //write to local db
                    for(Favorite favorite : (List<Favorite>)response.body())favoriteBox.put(favorite);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }
            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * delete a favorite in the database
     *
     * @param favorite
     * //@param handler
     */
    public void delete(final Favorite favorite, final FragmentHandler handler) {
        Call<Favorite> call = service.deleteFavorite(favorite);
        Favorite finalFavorite = favorite;
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                if (response.isSuccessful()) {
                    favoriteBox.remove(finalFavorite);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }



    public long count() {
        return favoriteBox.count();
    }

    /**
     * Return a list with all favorite tours
     *
     * @return List<Favorite>
     */
    public List<Favorite> find() {
        return favoriteBox.getAll();
    }

    /**
     * Searching for a single favorite tour with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return Favorite which match to the search pattern in the searched columns
     */
    public Favorite findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Favorite findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for favorite tour matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Favorite> which contains the equipments, which match to the search pattern in the searched columns
     */
    public List<Favorite> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Favorite> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Favorite> find(Property searchedColumn, boolean searchPattern) {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        favoriteBox.remove(findOne(searchedColumn, searchPattern));
    }


}

