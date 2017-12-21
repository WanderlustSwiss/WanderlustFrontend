package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.UserTourService;
import io.objectbox.Box;
import io.objectbox.Property;
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
    private static UserTourService service;
    Property columnProperty;
    private Box<UserTour> routeBox;

    /**
     * Constructor.
     */

    public UserTourDao() {
        routeBox = DatabaseController.boxStore.boxFor(UserTour.class);
        if (service == null) service = ServiceGenerator.createService(UserTourService.class);
    }

    public long count() {
        return routeBox.count();
    }

    public long count(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all tours which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * Update an existing usertour in the database.
     *
     * @param usertour (required).
     */
    public UserTour update(UserTour usertour) {
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
//                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()),response.body()));
//                } else {
//                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), null));
//                }
//            }
//            @Override
//            public void onFailure(Call<UserTour> call, Throwable t) {
//                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR,null));
//            }
//        });
//    }

    /**
     * get usertour out of the remote database by entity
     *
     * @param id
     * @param handler
     */
    public void retrieve(int id, final FragmentHandler handler) {
        Call<UserTour> call = service.retrieveUserTour(id);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if (response.isSuccessful()) {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * update a usertour
     *
     * @param id
     * @param usertour
     * @param handler
     */
    public void update(int id, final AbstractModel usertour, final FragmentHandler handler) {
        Call<UserTour> call = service.updateUserTour(id, (UserTour) usertour);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if (response.isSuccessful()) {
                    routeBox.put((UserTour) usertour);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * delete a usertour local and remote
     *
     * @param usertour
     * @param handler
     */
    public void delete(final AbstractModel usertour, final FragmentHandler handler) {
        Call<UserTour> call = service.deleteUserTour((UserTour) usertour);
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if (response.isSuccessful()) {
                    routeBox.remove((UserTour) usertour);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * get all usertours out of the remote database
     *
     * @param handler
     */
    public void retrieveAll(final FragmentHandler handler) {
        Call<UserTour> call = service.retrieveAllUserTours();
        call.enqueue(new Callback<UserTour>() {
            @Override
            public void onResponse(Call<UserTour> call, Response<UserTour> response) {
                if (response.isSuccessful())
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                else
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
            }

            @Override
            public void onFailure(Call<UserTour> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * @return
     */
    public List<UserTour> find() {
        if (routeBox != null)
            return routeBox.getAll();
        else
            return null;
    }

    /**
     * Searching for a single route with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return UserTour which match to the search pattern in the searched columns
     */
    public UserTour findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public UserTour findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for routes matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<UserTour> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<UserTour> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<UserTour> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<UserTour> find(Property searchedColumn, boolean searchPattern) {
        return routeBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }

    /**
     * delete:
     * Deleting a UserTour which matches the given pattern
     *
     * @param searchedColumn
     * @param searchPattern
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void deleteByPattern(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        routeBox.remove(findOne(searchedColumn, searchPattern));
    }
}
