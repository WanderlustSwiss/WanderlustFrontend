package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Trip
 * trip_id          integer	Primary Key, auto increment
 * communityTours	model	Foreign key, communityTours model
 * user	            model	Foreign key, user model
 * @author Alexander Weinbeck
 * @license MIT
 */

@Entity
public class Trip extends AbstractModel{

    @Id
    long trip_id;
    UserTour userTour ;
    User user;

    public Trip(long trip_id, UserTour usertour, User user) {
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

    public UserTour getUserTour() {
        return userTour;
    }

    public void setUserTour(UserTour userTour) {
        this.userTour = userTour;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
