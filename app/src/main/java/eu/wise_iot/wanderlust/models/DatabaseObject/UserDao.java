package eu.wise_iot.wanderlust.models.DatabaseObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.Event;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.User_;
import eu.wise_iot.wanderlust.services.PoiService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import eu.wise_iot.wanderlust.services.UserService;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        if(service == null){
            service = ServiceGenerator.createService(UserService.class);
        }
    }

    public long count(){
        return userBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = User_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(User_.class);
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        return userQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param user (required).
     *
     */
    public User update(final User user, final Context context){

        UserService service = ServiceGenerator.createService(UserService.class);

        Call<ResponseBody> call = service.changeEmail(user);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Fail: " + response, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        return user;
    }

    /**
     * Insert an user into the database.
     *
     * @param user (required).
     *
     */
    @Override
    public void create(final AbstractModel user, final FragmentHandler handler){

        Call<User> call = service.registerUser((User)user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    userBox.put((User)user);
                    handler.onResponse(new Event(Event.EventType.SUCCESSFUL, response.body()));
                } else{
                    handler.onResponse(new Event(Event.EventType.BAD_REQUEST, null));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                    handler.onResponse(new Event(Event.EventType.SERVER_ERROR, null));
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
        Field searchedField = User_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(User_.class);
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
        Field searchedField = User_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        Log.d("List<User> find()", searchedField.toString());

        columnProperty = (Property) searchedField.get(User_.class);
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        return userQuery.find();
    }

    public User delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        User toDeleteUser = findOne(searchedColumn, searchPattern);
        userBox.remove(toDeleteUser);
        return toDeleteUser;
    }

    public void deleteAll(){
        userBox.removeAll();
    }


}
