package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Equipment
 *
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Equipment extends AbstractModel {

    @Id
    long internal_id;
    long equip_id;
    String imagePath;
    String name;
    String description;
    long routeKit;

    public Equipment(long internal_id, long equip_id, String imagePath, String name, String description, long routeKit) {
        this.internal_id = internal_id;
        this.equip_id = equip_id;
        this.imagePath = imagePath;
        this.name = name;
        this.description = description;
        this.routeKit = routeKit;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public long getRouteKit() {
        return routeKit;
    }

    public void setRouteKit(long routeKit) {
        this.routeKit = routeKit;
    }

    public long getEquip_id() {
        return equip_id;
    }

    public void setEquip_id(long equip_id) {
        this.equip_id = equip_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
