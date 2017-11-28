package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Profile
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Profile extends AbstractModel{

    @Id
    long profile_id;
    String imagePath;
    int score;
    String birthday;
    String language;
    long user;
    long difficulty;

    public Profile(long profile_id, String imagePath, int score, String birthday, String language, long user, long difficulty) {
        this.profile_id = profile_id;
        this.imagePath = imagePath;
        this.score = score;
        this.birthday = birthday;
        this.language = language;
        this.user = user;
        this.difficulty = difficulty;
    }

    public String getLanguage() { return language; }

    public void setLanguage(String language) { this.language = language; }

    public long getUser() { return user; }

    public void setUser(long user) { this.user = user; }

    public long getDifficulty() { return difficulty; }

    public void setDifficulty(long difficulty) { this.difficulty = difficulty; }

    public long getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(long profile_id) {
        this.profile_id = profile_id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
