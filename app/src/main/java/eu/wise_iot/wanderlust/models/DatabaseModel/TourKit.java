package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Represents TourKit entity in the database
 *
 * @author Rilind Gashi
 * @license GPL-3.0
 */
@Entity
public class TourKit extends AbstractModel {

    @Id
    long internal_id;
    long rKit_id;
    long tour_id;
    long equip_id;

    public TourKit(long internal_id, long rKit_id, long tour, long equipment) {
        this.internal_id = internal_id;
        this.rKit_id = rKit_id;
        tour_id = tour;
        equip_id = equipment;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getrKit_id() {
        return rKit_id;
    }

    public void setrKit_id(long rKit_id) {
        this.rKit_id = rKit_id;
    }

    public long getTour() {
        return tour_id;
    }

    public void setTour(long tour) {
        tour_id = tour;
    }

    public long getEquipment() {
        return equip_id;
    }

    public void setEquipment(long equipment) {
        equip_id = equipment;
    }
}
