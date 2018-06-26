package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * represents the ViolationType and contains reference to given Violation
 *
 * @author Alexander Weinbeck
 * @license GPL-3.0
 */

@Entity
public class ViolationType extends AbstractModel {

    @Id
    long internal_id;
    final long violationt_id;
    final String name;
    final long score;

    public ViolationType(long internal_id, long violationt_id, String name, long score) {
        this.internal_id = internal_id;
        this.violationt_id = violationt_id;
        this.name = name;
        this.score = score;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getViolationt_id() { return violationt_id;}

}
