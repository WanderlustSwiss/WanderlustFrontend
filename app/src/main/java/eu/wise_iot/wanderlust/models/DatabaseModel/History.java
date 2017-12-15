package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * History
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class History extends AbstractModel{

    @Id
    long history_id;
    long absolvedRoute;

    public History(long history_id, long absolvedRoute) {
        this.history_id = history_id;
        this.absolvedRoute = absolvedRoute;
    }

    public long getHistory_id() {
        return history_id;
    }

    public void setHistory_id(long history_id) {
        this.history_id = history_id;
    }

    public long getAbsolvedRoute() {
        return absolvedRoute;
    }

    public void setAbsolvedRoute(long absolvedRoute) {
        this.absolvedRoute = absolvedRoute;
    }
}
