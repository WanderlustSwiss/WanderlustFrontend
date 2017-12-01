package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.TripService;
import eu.wise_iot.wanderlust.services.UserTourService;
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


public class UserTourDao extends DatabaseObjectAbstract {
    private Box<UserTour> routeBox;
    private Query<UserTour> routeQuery;
    private QueryBuilder<UserTour> routeQueryBuilder;
    Property columnProperty;

    private static UserTourService service;

//    /**
//     * Constructor.
//     *
//     * @param boxStore (required) delivers the connection to the frontend database
//     */
//
//    public UserTourDao(BoxStore boxStore){
//        routeBox = boxStore.boxFor(UserTour.class);
//        routeQueryBuilder = routeBox.query();
//
//        if(service == null) service = ServiceGenerator.createService(UserTourService.class);
//    }

    public long count(){
        return routeBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = UserTour.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(UserTour.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find().size();
    }

    /**
     * Update an existing usertour in the database.
     *
     * @param usertour (required).
     *
     */
    public UserTour update(UserTour usertour){
        routeBox.put(usertour);
        return usertour;
    }

    /**
     * insert a usertour local and remote
     * @param usertour
     * @param handler
     */
//    public void create(final AbstractModel usertour, final FragmentHandler handler){
//        Call<UserTour> call = service.createUserTour((UserTour)usertour);
//        call.enqueue(new Callback<UserTour>() {
//            @Override
//            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
//                if(response.isSuccessful()){
//                    routeBox.put((UserTour)usertour);
//                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
//                } else {
//                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
//                }
//            }
//            @Override
//            public void onFailure(Call<UserTour> call, Throwable t) {
//                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
//            }
//        });
//    }
    /**
     * get usertour out of the remote database by entity
     * @param id
     * @param handler
     */
    public void retrieve(int id, final FragmentHandler handler){
        Call<UserTour> call = service.retrieveUserTour(id);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if(response.isSuccessful()){
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * update a usertour
     * @param id
     * @param usertour
     * @param handler
     */
    public void update(int id, final AbstractModel usertour, final FragmentHandler handler){
        Call<UserTour> call = service.updateUserTour(id, (UserTour)usertour);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if(response.isSuccessful()){
                    routeBox.put((UserTour)usertour);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * delete a usertour local and remote
     * @param usertour
     * @param handler
     */
    public void delete(final AbstractModel usertour, final FragmentHandler handler){
        Call<UserTour> call = service.deleteUserTour((UserTour)usertour);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if(response.isSuccessful()){
                    routeBox.remove((UserTour)usertour);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * get all usertours out of the remote database
     * @param handler
     */
    public void retrieveAll(final FragmentHandler handler){
        Call<UserTour> call = service.retrieveAllUserTours();
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if(response.isSuccessful()) handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                else handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
            }
            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     *
     * @return
     */
    public List<UserTour> find() {
        return routeBox.getAll();
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return UserTour which match to the search pattern in the searched columns
     */
    public UserTour findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = UserTour.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(UserTour.class);
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
     * @return List<UserTour> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<UserTour> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = UserTour.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(UserTour.class);
        routeQueryBuilder.equal(columnProperty , searchPattern);
        routeQuery = routeQueryBuilder.build();
        return routeQuery.find();
    }

    /**
     * delete:
     * Deleting a UserTour which matches the given pattern
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