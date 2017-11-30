package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.TripService;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TripDao:
 *
 * @author Rilind Gashi, Alexander Weinbeck
 * @license MIT
 */


public class TripDao extends DatabaseObjectAbstract {
    private Box<Trip> routeBox;
    private Query<Trip> routeQuery;
    private QueryBuilder<Trip> routeQueryBuilder;
    Property columnProperty;

    private static TripService service;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public TripDao(BoxStore boxStore){
        routeBox = boxStore.boxFor(Trip.class);
        routeQueryBuilder = routeBox.query();

        if(service == null) service = ServiceGenerator.createService(TripService.class);
    }

    public long count(){
        return routeBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Trip.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Trip.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find().size();
    }

    /**
     * Update an existing Trip in the database.
     *
     * @param trip (required).
     *
     */
    public Trip update(Trip trip){
        routeBox.put(trip);
        return trip;
    }

    /**
     * insert a trip local and remote
     * @param trip
     * @param handler
     */
    public void create(int id, final AbstractModel trip, final FragmentHandler handler){
        Call<Trip> call = service.createTrip((Trip)trip);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if(response.isSuccessful()){
                    routeBox.put((Trip)trip);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * get trip out of the remote database by entity
     * @param id
     * @param handler
     */
    public void retrieve(int id, final FragmentHandler handler){
        Call<Trip> call = service.retrieveTrip(id);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if(response.isSuccessful()){
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * update a trip
     * @param id
     * @param trip
     * @param handler
     */
    public void update(int id, final AbstractModel trip, final FragmentHandler handler){
        Call<Trip> call = service.updateTrip(id, (Trip)trip);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if(response.isSuccessful()){
                    routeBox.put((Trip)trip);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * delete a trip local and remote
     * @param trip
     * @param handler
     */
    public void delete(final AbstractModel trip, final FragmentHandler handler){
        Call<Trip> call = service.deleteTrip((Trip)trip);
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if(response.isSuccessful()){
                    routeBox.remove((Trip)trip);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * get all trips out of the remote database
     * @param handler
     */
    public void retrieveAll(final FragmentHandler handler){
        Call<Trip> call = service.retrieveAllTrips();
        call.enqueue(new Callback<Trip>() {
            @Override
            public void onResponse(Call<Trip> call, Response<Trip> response) {
                if(response.isSuccessful()) handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                else handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
            }
            @Override
            public void onFailure(Call<Trip> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     *
     * @return
     */
    public List<Trip> find() {
        return routeBox.getAll();
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Trip which match to the search pattern in the searched columns
     */
    public Trip findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Trip.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Trip.class);
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
     * @return List<Trip> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<Trip> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Trip.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Trip.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find();
    }

    /**
     * delete:
     * Deleting a Trip which matches the given pattern
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
