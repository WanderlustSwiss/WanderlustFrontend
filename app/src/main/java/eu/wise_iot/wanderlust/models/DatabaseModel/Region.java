package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Region
 *
 * @author Simon Kaspar
 * @license MIT
 */

@Entity
public class Region extends AbstractModel implements Serializable {
    @Id
    long internal_id;
    long region_id;
    String countryCode;
    String name;

    public Region(long internal_id, long region_id, String name, String countryCode) {
        this.internal_id = internal_id;
        this.region_id = region_id;
        this.name = name;
        this.countryCode = countryCode;
    }
    public Region(){
        this.internal_id = 0;
        this.region_id = 0;
        this.name = "No name";
        this.countryCode = "NONE";
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getRegion_id() {
        return region_id;
    }

    public void setRegion_id(long region_id) {
        this.region_id = region_id;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() { return name; }
}
