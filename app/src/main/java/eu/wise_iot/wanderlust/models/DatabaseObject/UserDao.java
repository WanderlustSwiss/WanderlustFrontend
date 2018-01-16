package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.UserService;
import io.objectbox.Box;
import io.objectbox.Property;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * UserDao
 *
 * @author Rilind Gashi
 * @license MIT <license in our case always MIT>
 */

public class UserDao extends DatabaseObjectAbstract {

    private static UserService service;
    public Box<User> userBox;

    /**
     * Constructor.
     */

    public UserDao() {
        userBox = DatabaseController.boxStore.boxFor(User.class);
        service = ServiceGenerator.createService(UserService.class);
    }

    public long count() {
        return userBox.count();
    }

    public long count(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }

    /**
     * count all poi which match with the search criteria
     *
     * @return Total number of records
     */
    public long count(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return find(searchedColumn, searchPattern).size();
    }


    /**
     * Insert an user into the database.
     *
     * @param user    (required).
     * @param handler
     */
    public void create(final User user, final FragmentHandler handler) {
        Call<User> call = service.createUser(user);
        call.enqueue(new Callback<User>() {
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User newUser = response.body();
                    newUser.setUser_id(1);
                    newUser.setPassword(((User) user).getPassword());
                    userBox.put(newUser);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Retrieve a user out of the database
     *
     * @param handler
     */
    public void retrieve(final FragmentHandler handler) {
        Call<User> call = service.retrieveUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Update an existing user in the database.
     *
     * @param user    (required).
     * @param handler
     */
    public void update(final User user, final FragmentHandler handler) {

        UserService service = ServiceGenerator.createService(UserService.class);
        Call<User> call = service.updateUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userBox.removeAll();
                    userBox.put(response.body());
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Delete a user out of the database
     *
     * @param user
     * @param handler
     */
    public void delete(final AbstractModel user, final FragmentHandler handler) {
        Call<User> call = service.deleteUser((User) user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    userBox.remove((User) user);
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code()), response.body()));
                } else {
                    handler.onResponse(new ControllerEvent(EventType.getTypeByCode(response.code())));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new ControllerEvent(EventType.NETWORK_ERROR));
            }
        });
    }

    /**
     * Return a list with all user
     *
     * @return List<User>
     */
    public List<User> find() {
        return userBox.getAll();
    }


    public User getUser() {
        List<User> users = find();
        if (users.size() != 1) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Searching for a single user with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return User who match to the search pattern in the searched columns
     */
    public User findOne(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return userBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    public User findOne(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return userBox.query().equal(searchedColumn, searchPattern).build().findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern  (required) contain the search pattern.
     * @return List<User> which contains the users, who match to the search pattern in the searched columns
     */
    public List<User> find(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return userBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<User> find(Property searchedColumn, long searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        return userBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public List<User> find(Property searchedColumn, boolean searchPattern) {
        return userBox.query().equal(searchedColumn, searchPattern).build().find();
    }

    public void delete(Property searchedColumn, String searchPattern)
            throws NoSuchFieldException, IllegalAccessException {
        userBox.remove(findOne(searchedColumn, searchPattern));
    }

    /**
     * Deleting a user matching the corresponding searchpattern in given column
     *
     * @param searchedColumn
     * @param searchPattern
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void deleteByPattern(Property searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        User toDeleteUser = findOne(searchedColumn, searchPattern);
        userBox.remove(toDeleteUser);
        //return toDeleteUser;
    }

    /**
     * Delete all users out of the database
     */
    public void deleteAll() {

    }
}
