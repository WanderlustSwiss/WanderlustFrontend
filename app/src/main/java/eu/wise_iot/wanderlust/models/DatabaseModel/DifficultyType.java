package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Represents DifficultyType entity in database
 *
 * @author Rilind Gashi
 * @author Alexander Weinbeck
 * @license MIT
 */
@Entity
public class DifficultyType extends AbstractModel {
    @Id
    long internal_id;
    long difft_id;
    int level;
    String mark;
    String name;
    String description;
    long userDifficulty;
    long routeDifficulty;

    public DifficultyType(long internal_id, long difft_id, int level, String mark, String name, String description, long userDifficulty, long routeDifficulty) {
        this.internal_id = internal_id;
        this.difft_id = difft_id;
        this.level = level;
        this.mark = mark;
        this.name = name;
        this.description = description;
        this.userDifficulty = userDifficulty;
        this.routeDifficulty = routeDifficulty;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserDifficulty() {
        return userDifficulty;
    }

    public void setUserDifficulty(long userDifficulty) {
        this.userDifficulty = userDifficulty;
    }

    public long getRouteDifficulty() {
        return routeDifficulty;
    }

    public void setRouteDifficulty(long routeDifficulty) {
        this.routeDifficulty = routeDifficulty;
    }

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
