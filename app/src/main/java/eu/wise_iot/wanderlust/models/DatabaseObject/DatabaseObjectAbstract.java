package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.User_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.Property;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

/**
 * DatabaseObjectAbstract
 * @author Rilind Gashi
 * @license MIT
 */

public abstract class DatabaseObjectAbstract implements DatabaseObject{

    public AbstractModel create(AbstractModel abstractModel){
        throw new UnsupportedOperationException();
    }

    public AbstractModel update(AbstractModel abstractModel){
        throw new UnsupportedOperationException();
    }

    public abstract List<? extends AbstractModel> find();

    public AbstractModel findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        throw new UnsupportedOperationException();
    }

    public List<? extends AbstractModel> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException {
        throw new UnsupportedOperationException();

    }

    public AbstractModel delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException{
        throw new UnsupportedOperationException();
    }

    public int count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException{
        throw new UnsupportedOperationException();
    }


}