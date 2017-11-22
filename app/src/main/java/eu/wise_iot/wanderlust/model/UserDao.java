package eu.wise_iot.wanderlust.model;

import android.content.Context;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * User Dataobject
 *
 * <P>Handles the database queries for User Object
 *
 *
 * @author Rilind Gashi
 * @version 1.0
 */

public class UserDao {

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

    /**
     * Insert an user into the database.
     *
     * @param user (required).
     */
    public void insertUser(User user){
        userBox.put(user);
    }

    /**
     * Return a list with all user
     *
     * @return List<User>
     */
    public List<User> findAll() {
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
    public User findOne(String searchedColumn, String searchPattern){
        switch (searchedColumn) {
            case "id":
                columnProperty = User_.id;
                break;
            case "nickname":
                columnProperty = User_.nickname;
                break;
            case "mail":
                columnProperty = User_.mail;
                break;
            case "password":
                columnProperty = User_.password;
                break;
        }
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
    public List<User> findWithFilter(String searchedColumn, String searchPattern){
        switch (searchedColumn) {
            case "id":
                columnProperty = User_.id;
                break;
            case "nickname":
                columnProperty = User_.nickname;
                break;
            case "mail":
                columnProperty = User_.mail;
                break;
            case "password":
                columnProperty = User_.password;
                break;
        }
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        return userQuery.find();
    }

    //Wird nicht benötigt, da man im Frontend den User nicht löschen kann.
    //Habe ich aber als Beispiel gelassen, falls du es dir anschauen möchtest
    /*
    public void deleteUser(String searchedColumn, String searchPattern){
        switch (searchedColumn) {
            case "id":
                columnProperty = User_.id;
                break;
            case "nickname":
                columnProperty = User_.nickname;
                break;
            case "mail":
                columnProperty = User_.mail;
                break;
            case "password":
                columnProperty = User_.password;
                break;
        }
        userQueryBuilder.equal(columnProperty , searchPattern);
        userQuery = userQueryBuilder.build();
        userQuery.remove();
    }
    */

}
