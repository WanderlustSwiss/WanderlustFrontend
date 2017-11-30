package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.TourService;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TourDao
 * @author Rilind Gashi
 * @license MIT
 */


public class TourDao extends DatabaseObjectAbstract {
    private Box<Tour> routeBox;
    private Query<Tour> routeQuery;
    private QueryBuilder<Tour> routeQueryBuilder;
    Property columnProperty;

    private static TourService service;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public TourDao(BoxStore boxStore){
        routeBox = boxStore.boxFor(Tour.class);
        routeQueryBuilder = routeBox.query();

        if(service == null) service = ServiceGenerator.createService(TourService.class);
    }

    public long count(){
        return routeBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Tour.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Tour.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param tour (required).
     *
     */
    public Tour update(Tour tour){
        routeBox.put(tour);
        return tour;
    }
    /**
     * get all tours out of the database
     * @param tour
     * @param handler
     */
    public void retrieveAll(final AbstractModel tour, final FragmentHandler handler){
        Call<Tour> call = service.retrieveAllTours();
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()) handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                else handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
            }
            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * get tour out of the database by entity
     * @param tour
     * @param handler
     */
    public void retrieve(int id, final AbstractModel tour, final FragmentHandler handler){
        Call<Tour> call = service.retrieveTour(id);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * insert a tour
     * @param tour
     * @param handler
     */
    public void create(final AbstractModel tour, final FragmentHandler handler){
        Call<Tour> call = service.createTour((Tour)tour);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    routeBox.put((Tour)tour);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * delete a tour
     * @param tour
     * @param handler
     */
    public void delete(final AbstractModel tour, final FragmentHandler handler){
        Call<Tour> call = service.deleteTour((Tour)tour);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    routeBox.remove((Tour)tour);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * update a tour
     * @param tour
     * @param handler
     */
    public void update(final AbstractModel tour, final FragmentHandler handler){
        Call<Tour> call = service.updateTour((Tour)tour);
        call.enqueue(new Callback<Tour>() {
            @Override
            public void onResponse(Call<Tour> call, Response<Tour> response) {
                if(response.isSuccessful()){
                    routeBox.put((Tour)tour);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Tour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     *
     * @return
     */
    public List<Tour> find() {
        return routeBox.getAll();
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Tour which match to the search pattern in the searched columns
     */
    public Tour findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Tour.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Tour.class);
        routeQueryBuilder.equal(columnProperty, searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.findFirst();
    }

    /**
     * Searching for routes matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Tour> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<Tour> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Tour.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Tour.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find();
    }

    /**
     * delete:
     * Deleting a Tour which matches the given pattern
     * @param searchedColumn
     * @param searchPattern
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void deleteByPattern(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }
}
