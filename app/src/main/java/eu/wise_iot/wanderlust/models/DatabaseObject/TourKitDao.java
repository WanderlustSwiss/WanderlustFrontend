package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.models.DatabaseModel.TourKit;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * TourKitDao
 * @author Rilind Gashi
 * @license MIT
 */

public class TourKitDao extends DatabaseObjectAbstract{

    private Box<TourKit> tourEquipementBox;
    private Query<TourKit> tourEquipementQuery;
    private QueryBuilder<TourKit> tourEquipementQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     */

    public TourKitDao(){
        tourEquipementBox = DatabaseController.boxStore.boxFor(TourKit.class);
        tourEquipementQueryBuilder = tourEquipementBox.query();
    }

    public long count(){
        return tourEquipementBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = TourKit.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(TourKit.class);
        tourEquipementQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        tourEquipementQuery = tourEquipementQueryBuilder.build();
        return tourEquipementQuery.find().size();
    }


    public TourKit update(TourKit tourKit){
        return null;
    }

    /**
     * Insert an user into the database.
     *
     * @param tourKit (required).
     *
     */
    public void create(TourKit tourKit){
        tourEquipementBox.put(tourKit);
    }

    /**
     * Return a list with all tourEquipement
     *
     * @return List<TourKit>
     */
    public List<TourKit> find() {
        return tourEquipementBox.getAll();
    }

    /**
     * Searching for a single tour equipement with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return CommunityTours Equipment which match to the search pattern in the searched columns
     */
    public TourKit findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = TourKit.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(TourKit.class);
        tourEquipementQueryBuilder.equal(columnProperty, Integer.valueOf(searchPattern));
        tourEquipementQuery = tourEquipementQueryBuilder.build();
        return tourEquipementQuery.findFirst();
    }

    /**
     * Searching for user matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<TourKit> which contains the tour equipements, which match to the search pattern in the searched columns
     */
    public List<TourKit> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = TourKit.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(TourKit.class);
        tourEquipementQueryBuilder.equal(columnProperty , Integer.valueOf(searchPattern));
        tourEquipementQuery = tourEquipementQueryBuilder.build();
        return tourEquipementQuery.find();
    }

    public void delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        tourEquipementBox.remove(findOne(searchedColumn, searchPattern));
    }

    public void deleteAll(){
        tourEquipementBox.removeAll();
    }


}
