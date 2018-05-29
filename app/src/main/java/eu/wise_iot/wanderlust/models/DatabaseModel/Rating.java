package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Represents a User in the database
 *
 * @author Rilind Gashi
 * @license MIT
 */
@Entity
public class Rating extends AbstractModel {
    @Id
    long internal_id;
    long rat_id;
    int rate;
    long tour;
    long user;

    public Rating(long internal_id, long rat_id, int rate, long tour, long user) {
        this.internal_id = internal_id;
        this.rat_id = rat_id;
        this.rate = rate;
        this.tour = tour;
        this.user = user;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getRat_id() {
        return rat_id;
    }

    public void setRat_id(long rat_id) {
        this.rat_id = rat_id;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
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
