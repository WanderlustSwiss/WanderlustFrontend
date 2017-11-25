package eu.wise_iot.wanderlust.model.DatabaseModel;

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
    long profileId;
    String birthdate;
    int score;
    String picturePath;

    public Profile(long profileId, String birthdate, int score, String picturePath) {
        this.profileId = profileId;
        this.birthdate = birthdate;
        this.score = score;
        this.picturePath = picturePath;
    }

    public long getProfileId() {
        return profileId;
    }

    public void setProfileId(long profileId) {
        this.profileId = profileId;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
