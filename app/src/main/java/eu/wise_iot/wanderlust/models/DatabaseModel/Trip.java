package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Represents a Trip entity in the database
 * trip_id          integer	Primary Key, auto increment
 * communityTours	model	Foreign key, communityTours model
 * user	            model	Foreign key, user model
 *
 * @author Alexander Weinbeck
 * @author Tobias Ruegsegger
 * @license GPL-3.0
 */
@Entity
public class Trip extends AbstractModel {

    @Id
    long trip_id;
    long tour;
    long user;

    public Trip(long trip_id, long usertour, long user) {
        this.trip_id = trip_id;
        tour = usertour;
        this.user = user;
    }

    public Trip(){

    }

    public long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(long trip_id) {
        this.trip_id = trip_id;
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
