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
    long userTour_id;
    long userTourModel;

    public CommunityTour(int userTour_id, long userTourModel) {
        this.userTour_id = userTour_id;
        this.userTourModel = userTourModel;
    }

    public long getUserTour_id() {
        return userTour_id;
    }

    public void setUserTour_id(long userTour_id) {
        this.userTour_id = userTour_id;
    }

    public long getUserTourModel() {
        return userTourModel;
    }

    public void setUserTourModel(long userTourModel) {
        this.userTourModel = userTourModel;
    }
}
