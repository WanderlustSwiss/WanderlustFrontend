package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.util.Log;

import java.util.List;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.ImageController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite_;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.services.FavoriteService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
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

    private static class Holder {
        private static final FavoriteDao INSTANCE = new FavoriteDao();
    }

    private static final BoxStore BOXSTORE = DatabaseController.getBoxStore();
    private static final String TAG = "FavoriteDao";

    public static FavoriteDao getInstance(){
        return BOXSTORE != null ? Holder.INSTANCE : null;
    }

    private final Box<Favorite> favoriteBox;
    private final FavoriteService service;
    private final ImageController imageController;

    private FavoriteDao(){
        favoriteBox = BOXSTORE.boxFor(Favorite.class);
        service = ServiceGenerator.createService(FavoriteService.class);
        imageController = ImageController.getInstance();
    }

    /**
     * Insert a favorite into the database
     *
     * //@param handler
     */
    public void create(Tour tour, final FragmentHandler handler) {
        Call<Favorite> call = service.createFavorite(tour);
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
     * Retriev all favorite tours
     *
     * @param handler
     */
    public void retrieveAllFavoriteTours(final FragmentHandler handler) {
        Call<List<Tour>> call = service.retrieveAllFavoriteTours();
        call.enqueue(new Callback<List<Tour>>() {
            @Override
            public void onResponse(Call<List<Tour>> call, retrofit2.Response<List<Tour>> response) {
                if (response.isSuccessful()) {
                    List<Tour> tours = response.body();
                    for (Tour tour : tours) {
                        for (ImageInfo imageInfo : tour.getImagePaths()) {
                            String name = tour.getTour_id() + "-" + imageInfo.getId() + ".jpg";
                            imageInfo.setName(name);
                            imageInfo.setLocalDir(imageController.getTourFolder());
                        }
                    }
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
     * Insert all favorites into the database
     *
     */
    public void retrieveAllFavorites() {
        Call<List<Favorite>> call = service.retrievAllFavorites();
        call.enqueue(new Callback<List<Favorite>>() {
            @Override
            public void onResponse(Call<List<Favorite>> call, retrofit2.Response<List<Favorite>> response) {
                if (response.isSuccessful()) {
                    //write to local db
                    favoriteBox.removeAll();
                    for(Favorite favorite : response.body()){
                        favorite.setInternal_id(0);
                        favoriteBox.put(favorite);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Favorite>> call, Throwable t) {
            }
        });
    }
    /**
     * delete a favorite in the database
     *
     * @param favorite_id
     * @param handler
     */
    public void delete(long favorite_id, final FragmentHandler handler) {
        Call<Favorite> call = service.deleteFavorite(favorite_id);
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                if (response.isSuccessful()) {
                    try {
                        Favorite favorite = findOne(Favorite_.fav_id, favorite_id);
                        if (favorite != null) {
                            favoriteBox.remove(favorite.getInternal_id());
                            handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                        }
                    } catch (Exception e) {
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                        if (BuildConfig.DEBUG) Log.d(TAG, "Favorite delete failed" + e.getMessage() + e.getStackTrace() + e.getCause());
                    }
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<Favorite> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    public void retrieve(long id, final FragmentHandler handler) {
        Call<Favorite> call = service.retrieveFavorite(id);
        call.enqueue(new Callback<Favorite>() {
            @Override
            public void onResponse(Call<Favorite> call, Response<Favorite> response) {
                if (response.isSuccessful()) {
                        Favorite backendFavorite = response.body();
                        favoriteBox.put(backendFavorite);
                        handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), backendFavorite));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
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
    @SuppressWarnings("WeakerAccess")
    public Favorite findOne(Property searchedColumn, String searchPattern) {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public Favorite findOne(Property searchedColumn, long searchPattern) {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for favorite tour matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<Favorite> which contains the equipments, which match to the search pattern in the searched columns
     */
    public List<Favorite> find(Property searchedColumn, String searchPattern) {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Favorite> find(Property searchedColumn, long searchPattern) {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<Favorite> find(Property searchedColumn, boolean searchPattern) {
        return favoriteBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern) {
        favoriteBox.remove(findOne(searchedColumn, searchPattern));
    }


}

