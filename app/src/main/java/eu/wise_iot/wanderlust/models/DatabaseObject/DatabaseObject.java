package eu.wise_iot.wanderlust.models.DatabaseObject;

import java.util.List;

import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;

/**
 * DatabaseObject
 *
 * @author Rilind Gashi
 * @license MIT <license in our case always MIT>
 */

public interface DatabaseObject {

    void create(final AbstractModel abstractModel, final FragmentHandler handler);

    void update(final AbstractModel abstractModel, final FragmentHandler handler);

    void delete(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;

    List<? extends AbstractModel> find();

    List<? extends AbstractModel> find(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;

    AbstractModel findOne(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;

    long count(String searchedColumn, String searchPattern) throws NoSuchFieldException, IllegalAccessException;

}
