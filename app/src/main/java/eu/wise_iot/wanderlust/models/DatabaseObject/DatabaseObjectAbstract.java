package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;
import eu.wise_iot.wanderlust.models.DatabaseModel.User_;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;

/**
 * DatabaseObjectAbstract
 * @author Rilind Gashi
 * @license MIT
 */

public abstract class DatabaseObjectAbstract implements DatabaseObject{

    public void create(final AbstractModel abstractModel, final FragmentHandler handler){
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

    public long count(){
        throw new UnsupportedOperationException();
    }

    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException{
        throw new UnsupportedOperationException();
    }


}