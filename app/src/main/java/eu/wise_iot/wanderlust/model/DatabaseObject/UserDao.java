package eu.wise_iot.wanderlust.model.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.model.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.model.DatabaseModel.User;
import eu.wise_iot.wanderlust.model.DatabaseModel.User_;
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
    private Property columnProperty = User_.id;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public UserDao(BoxStore boxStore){
        userBox = boxStore.boxFor(User.class);
        userQueryBuilder = userBox.query();
    }

    public int count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        return 0;
    }

    public User update(User user){
        return null;
    }
    /**
     * Insert an user into the database.
     *
     * @param user (required).
     *
     */
    public void create(User user){
        userBox.put(user);
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
        Field searchedField = User_.class.getClass().getDeclaredField(searchedColumn);
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
        Field searchedField = User_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(User_.class);
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        return userQuery.find();
    }

    public User delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = User_.class.getClass().getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(User_.class);
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        userQuery.remove();
        return null;
    }


}
