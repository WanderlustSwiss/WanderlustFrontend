package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PoiType extends AbstractModel {
    @Id
    long internal_id;

    long poit_id;
    String name;

    public PoiType(long internal_id, long poit_id, String name) {
        this.internal_id = internal_id;
        this.poit_id = poit_id;
        this.name = name;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getPoit_id() {
        return poit_id;
    }

    public void setPoit_id(long poit_id) {
        this.poit_id = poit_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
