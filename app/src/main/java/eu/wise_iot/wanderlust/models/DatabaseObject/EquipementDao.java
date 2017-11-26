package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.lang.reflect.Field;
import java.util.List;

import eu.wise_iot.wanderlust.model.DatabaseModel.Equipement;
import eu.wise_iot.wanderlust.model.DatabaseModel.Equipement_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * EquipementDao
 * @author Rilind Gashi
 * @license MIT
 */

public class EquipementDao extends DatabaseObjectAbstract {
    private Box<Equipement> equipementBox;
    private Query<Equipement> equipementQuery;
    private QueryBuilder<Equipement> equipementQueryBuilder;
    Property columnProperty;

    /**
     * Constructor.
     *
     * @param boxStore (required) delivers the connection to the frontend database
     */

    public EquipementDao(BoxStore boxStore){
        equipementBox = boxStore.boxFor(Equipement.class);
        equipementQueryBuilder = equipementBox.query();
    }

    public long count(){
        return equipementBox.count();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Equipement_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Equipement_.class);
        equipementQueryBuilder.equal(columnProperty , searchPattern);
        equipementQuery = equipementQueryBuilder.build();
        return equipementQuery.find().size();
    }

    /**
     * Update an existing difficulty in the database.
     *
     * @param equipement (required).
     *
     */
    public Equipement update(Equipement equipement){
        equipementBox.put(equipement);
        return equipement;
    }
    /**
     * Insert an equipement into the database.
     *
     * @param equipement (required).
     *
     */
    public void create(Equipement equipement){
        equipementBox.put(equipement);
    }

    /**
     * Return a list with all equipement
     *
     * @return List<Equipement>
     */
    public List<Equipement> find() {
        return equipementBox.getAll();
    }

    /**
     * Searching for a single equipement with a search pattern in a column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return Equipement which match to the search pattern in the searched columns
     */
    public Equipement findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Equipement_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Equipement_.class);
        equipementQueryBuilder.equal(columnProperty, searchPattern);
        equipementQuery = equipementQueryBuilder.build();
        return equipementQuery.findFirst();
    }

    /**
     * Searching for equipement matching with the search pattern in a the selected column.
     *
     * @param searchedColumn (required) the column in which the searchPattern should be looked for.
     * @param searchPattern (required) contain the search pattern.
     *
     * @return List<Equipement> which contains the equipements, which match to the search pattern in the searched columns
     */
    public List<Equipement> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        Field searchedField = Equipement_.class.getDeclaredField(searchedColumn);
        searchedField.setAccessible(true);

        columnProperty = (Property) searchedField.get(Equipement_.class);
        equipementQueryBuilder.equal(columnProperty , searchPattern);
        equipementQuery = equipementQueryBuilder.build();
        return equipementQuery.find();
    }

    public Equipement delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        equipementBox.remove(findOne(searchedColumn, searchPattern));

        return null;
    }

    public void deleteAll(){
        equipementBox.removeAll();
    }

}
