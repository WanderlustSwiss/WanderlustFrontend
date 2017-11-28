package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * RouteKit
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class RouteKit extends AbstractModel{

    @Id
    long rKit_id;
    long route;
    long equipment;

    public RouteKit(long rKit_id, long route, long equipment) {
        this.rKit_id = rKit_id;
        this.route = route;
        this.equipment = equipment;
    }

    public long getrKit_id() {
        return rKit_id;
    }

    public void setrKit_id(long rKit_id) { this.rKit_id = rKit_id; }

    public long getRoute() {
        return route;
    }

    public void setRoute(long route) {
        this.route = route;
    }

    public long getEquipment() {
        return equipment;
    }

    public void setEquipment(long equipment) {
        this.equipment = equipment;
    }
}
