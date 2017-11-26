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
    long historyId;
    long absolvedRoute;

    public History(long historyId, long absolvedRoute) {
        this.historyId = historyId;
        this.absolvedRoute = absolvedRoute;
    }

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public long getAbsolvedRoute() {
        return absolvedRoute;
    }

    public void setAbsolvedRoute(long absolvedRoute) {
        this.absolvedRoute = absolvedRoute;
    }
}
