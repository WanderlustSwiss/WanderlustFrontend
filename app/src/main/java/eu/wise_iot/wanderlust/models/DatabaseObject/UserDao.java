package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.util.Log;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;


/**
 * UserDao
 * @author Rilind Gashi
 * @license MIT <license in our case always MIT>
 */

public class UserDao extends DatabaseObjectAbstract{

    private Box<User> userBox;
    private Query<User> userQuery;
    private QueryBuilder<User> userQueryBuilder;
    private Property columnProperty;// = User_.id;

    private static UserService service;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public UserDao(BoxStore boxStore){
        userBox = boxStore.boxFor(User.class);
        userQueryBuilder = userBox.query();

        if(service == null) service = ServiceGenerator.createService(UserService.class);
    }

    public long count(){
        return userBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = User.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(User.class);
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        return userQuery.find().size();
    }

    /**
     * Insert an user into the database.
     *
     * @param user (required).
     * @param handler
     */
    @Override
    public void create(final AbstractModel user, final FragmentHandler handler){
        Call<User> call = service.createUser((User)user);
        call.enqueue(new Callback<User>() {
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    User newUser = response.body();
                    newUser.setUser_id(1);
                    newUser.setPassword(((User) user).getPassword());
                    userBox.put(newUser);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
    }
    /**
     * Retrieve a user out of the database
     * @param handler
     */
    public void retrieve(final FragmentHandler handler){
        Call<User> call = service.retrieveUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * Update an existing user in the database.
     *
     * @param user (required).
     * @param handler
     */
    public void update(final AbstractModel user, final FragmentHandler handler){

        //UserService service = ServiceGenerator.createService(UserService.class);
        Call<User> call = service.updateUser((User)user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    userBox.put(response.body());
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
            }
        });
    }
    /**
     * Delete a user out of the database
     * @param user
     * @param handler
     */
    public void delete(final AbstractModel user, final FragmentHandler handler){
        Call<User> call = service.deleteUser((User)user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    userBox.remove((User)user);
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()),response.body()));
                } else {
                    handler.onResponse(new Event(EventType.getTypeByCode(response.code()), null));
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handler.onResponse(new Event(EventType.NETWORK_ERROR,null));
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

    /**
     * Searching for a single user with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return User who match to the search pattern in the searched columns
     */
    public User findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = User.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(User.class);
        userQueryBuilder.equal(columnProperty, searchPattern);
        userQuery = userQueryBuilder.build();
        return userQuery.findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<User> which contains the users, who match to the search pattern in the searched columns
     */
    public List<User> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = User.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        Log.d("List<User> find()", searchedField.toString());

        columnProperty = (Property) searchedField.get(User.class);
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        return userQuery.find();
    }

    /**
     * Deleting a user matching the corresponding searchpattern in given column
     * @param searchedColumn
     * @param searchPattern
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public void deleteByPattern(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        User toDeleteUser = findOne(searchedColumn, searchPattern);
        userBox.remove(toDeleteUser);
        //return toDeleteUser;
    }

    /**
     * Delete all users out of the database
     */
    public void deleteAll(){

    }
}
