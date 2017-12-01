package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PoiType extends AbstractModel {
    @Id
    long poit_id;
    String name;

    public PoiType(long poit_id, String name) {
        this.poit_id = poit_id;
        this.name = name;
    }

    public long getPoit_id() { return poit_id; }

    public void setPoit_id(long poit_id) { this.poit_id = poit_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
