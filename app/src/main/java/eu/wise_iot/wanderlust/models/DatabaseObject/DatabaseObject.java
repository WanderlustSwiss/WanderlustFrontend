package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.io.File;
import java.util.List;

import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;

/**
 * DatabaseObject
 * @author Rilind Gashi
 * @license MIT <license in our case always MIT>
 */

public interface DatabaseObject {

    public void create(final AbstractModel abstractModel, final FragmentHandler handler);
    void addImage(final File file, final int poiId);
    public AbstractModel update(AbstractModel abstractModel);
    public AbstractModel delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;
    public List<? extends AbstractModel> find();
    public List<? extends AbstractModel> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException ;
    public AbstractModel findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException ;
    public long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;

}
