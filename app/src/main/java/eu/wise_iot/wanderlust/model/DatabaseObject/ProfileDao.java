package eu.wise_iot.wanderlust.model.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.model.DatabaseModel.Profile_;
import eu.wise_iot.wanderlust.model.DatabaseModel.Profile;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * ProfileDao
 * @author Rilind Gashi
 * @license MIT
 */

public class ProfileDao extends DatabaseObjectAbstract {
    private Box<Profile> profileBox;
    private Query<Profile> profileQuery;
    private QueryBuilder<Profile> profileQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public ProfileDao(BoxStore boxStore){
        profileBox = boxStore.boxFor(Profile.class);
        profileQueryBuilder = profileBox.query();
    }

    public long count(){
        return profileBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Profile_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Profile_.class);
        profileQueryBuilder.equal(columnProperty , searchPattern);
        profileQuery = profileQueryBuilder.build();
        return profileQuery.find().size();
    }

    /**
     * Update an existing user in the database.
     *
     * @param profile (required).
     *
     */
    public Profile update(Profile profile){
        profileBox.put(profile);
        return profile;
    }
    /**
     * Insert a profile into the database.
     *
     * @param profile (required).
     *
     */
    public void create(Profile profile){
        profileBox.put(profile);
    }

    /**
     * Return a list with all profiles
     *
     * @return List<Profile>
     */
    public List<Profile> find() {
        return profileBox.getAll();
    }

    /**
     * Searching for a single profile with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Profile who match to the search pattern in the searched columns
     */
    public Profile findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Profile_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Profile_.class);
        profileQueryBuilder.equal(columnProperty, searchPattern);
        profileQuery = profileQueryBuilder.build();
        return profileQuery.findFirst();
    }

    /**
     * Searching for profile matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Profile> which contains the users, who match to the search pattern in the searched columns
     */
    public List<Profile> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Profile_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Profile_.class);
        profileQueryBuilder.equal(columnProperty , searchPattern);
        profileQuery = profileQueryBuilder.build();
        return profileQuery.find();
    }

    public Profile delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        profileBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        profileBox.removeAll();
    }

}
