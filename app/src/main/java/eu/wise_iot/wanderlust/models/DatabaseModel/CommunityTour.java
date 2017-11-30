package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * CommunityTour
 * @author Alexander Weinbeck
 * @license MIT
 */

@Entity
public class CommunityTour extends AbstractModel{

    @Id
    int userTour_id;
    UserTour userTourModel;

    public CommunityTour(int userTour_id, UserTour userTourModel) {
        this.userTour_id = userTour_id;
        this.userTourModel = userTourModel;
    }

    public int getUserTour_id() {
        return userTour_id;
    }

    public void setUserTour_id(int userTour_id) {
        this.userTour_id = userTour_id;
    }

    public UserTour getUserTourModel() {
        return userTourModel;
    }

    public void setUserTourModel(UserTour userTourModel) {
        this.userTourModel = userTourModel;
    }
}
