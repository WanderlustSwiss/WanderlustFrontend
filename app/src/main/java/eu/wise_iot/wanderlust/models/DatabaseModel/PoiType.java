package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class PoiType extends AbstractModel {
    @Id
    long poiType_id;
    String name;

    public PoiType(long poiType_id, String name) {
        this.poiType_id = poiType_id;
        this.name = name;
    }

    public long getPoiType_id() { return poiType_id; }

    public void setPoiType_id(long poiType_id) { this.poiType_id = poiType_id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }
}
