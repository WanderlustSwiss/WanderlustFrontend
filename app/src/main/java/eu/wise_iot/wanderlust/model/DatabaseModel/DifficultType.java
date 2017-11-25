package eu.wise_iot.wanderlust.model.DatabaseModel;

import android.support.annotation.IntDef;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * DifficultType
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class DifficultType extends AbstractModel{
    @Id
    long id;
    String typename;

    public DifficultType(long id, String typename) {
        this.id = id;
        this.typename = typename;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }
}
