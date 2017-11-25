package eu.wise_iot.wanderlust.model.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.model.DatabaseModel.AbstractModel;

/**
 * DatabaseObject
 * @author Rilind Gashi
 * @license MIT <license in our case always MIT>
 */

public interface DatabaseObject {

    public AbstractModel create(AbstractModel abstractModel);
    public AbstractModel update(AbstractModel abstractModel);
    public AbstractModel delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;
    public List<? extends AbstractModel> find();
    public List<? extends AbstractModel> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException ;
    public AbstractModel findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException ;
    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;

}
