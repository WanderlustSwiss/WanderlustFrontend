package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;

/**
 * Trip
 * trip_id          integer	Primary Key, auto increment
 * communityTours	model	Foreign key, communityTours model
 * user	            model	Foreign key, user model
 * @author Alexander Weinbeck, Tobias Ruegsegger
 * @license MIT
 */

@Entity
public class Trip extends AbstractModel{

    @Id
    long trip_id;
    long userTour ;
    long user;

    public Trip(long trip_id, long usertour, long user) {
        this.trip_id = trip_id;
        this.userTour = usertour;
        this.user = user;
    }

    public long getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(long trip_id) {
        this.trip_id = trip_id;
    }

    public long getUserTour() {
        return userTour;
    }

    public void setUserTour(long userTour) {
        this.userTour = userTour;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }
}
