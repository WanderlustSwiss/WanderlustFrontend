package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * DifficultType
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class DifficultType extends AbstractModel{
    @Id
    long difft_id;
    int level;
    String name;
    String description;
    long userDifficulty;
    long routeDifficulty;

    public DifficultType(long difft_id, int level, String name, String description, long userDifficulty, long routeDifficulty) {
        this.difft_id = difft_id;
        this.level = level;
        this.name = name;
        this.description = description;
        this.userDifficulty = userDifficulty;
        this.routeDifficulty = routeDifficulty;
    }

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public long getUserDifficulty() { return userDifficulty; }

    public void setUserDifficulty(long userDifficulty) { this.userDifficulty = userDifficulty; }

    public long getRouteDifficulty() { return routeDifficulty; }

    public void setRouteDifficulty(long routeDifficulty) { this.routeDifficulty = routeDifficulty; }

    public long getDifft_id() {
        return difft_id;
    }

    public void setDifft_id(long difft_id) {
        this.difft_id = difft_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
