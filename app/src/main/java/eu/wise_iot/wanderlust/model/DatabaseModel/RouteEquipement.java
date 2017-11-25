package eu.wise_iot.wanderlust.model.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * RouteEquipement
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class RouteEquipement extends AbstractModel{

    @Id
    long routeEquipementId;
    long routeId;
    long equipementId;

    public RouteEquipement(long routeEquipementId, long routeId, long equipementId) {
        this.routeEquipementId = routeEquipementId;
        this.routeId = routeId;
        this.equipementId = equipementId;
    }

    public long getRouteEquipementId() {
        return routeEquipementId;
    }

    public void setRouteEquipementId(long routeEquipementId) {
        this.routeEquipementId = routeEquipementId;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public long getEquipementId() {
        return equipementId;
    }

    public void setEquipementId(long equipementId) {
        this.equipementId = equipementId;
    }
}
