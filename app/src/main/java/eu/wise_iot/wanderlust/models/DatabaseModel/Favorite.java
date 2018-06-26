package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Represents Favorite entity in database
 *
 * @author Rilind Gashi
 * @license GPL-3.0
 */
@Entity
public class Favorite extends AbstractModel{

    @Id
    long internal_id;
    long fav_id;
    long tour;
    long user;

    public Favorite(long internal_id, long fav_id, long tour, long user) {
        this.internal_id = internal_id;
        this.fav_id = fav_id;
        this.tour = tour;
        this.user = user;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getFav_id() {
        return fav_id;
    }

    public void setFav_id(long fav_id) {
        this.fav_id = fav_id;
    }

    public long getTour() {
        return tour;
    }

    public void setTour(long tour) {
        this.tour = tour;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }
}
