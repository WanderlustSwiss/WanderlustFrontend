package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * User
 *
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class User extends AbstractModel {

    @Id
    long internalId;
    long user_id;
    String nickname;
    String email;
    String password;
    long profile;
    boolean isActive;
    boolean isValid;
    String lastLogin;
    String accountType;

    /**
     * Constructor.
     *
     * @param user_id  (required) userid which is needed for saving into the database. Should be a long.
     * @param nickname (required) nickname of the user
     * @param email    (required) email of the user
     * @param password (required) password of the user
     */
    public User(long user_id, String nickname, String email, String password, long profile,
                boolean isActive, boolean isValid, String lastLogin, String accountType) {
        internalId = 0;
        this.user_id = user_id;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.isActive = isActive;
        this.isValid = isValid;
        this.lastLogin = lastLogin;
        this.accountType = accountType;
    }

    public User() {
        internalId = 0;
        user_id = 1;
        nickname = "test nickname";
        email = "muster@mustermail.com";
        password = "test";
        profile = 1;
        isActive = false;
        isValid = false;
        lastLogin = "";
        accountType = "";
    }


    public void setInternalId(long id) {
        internalId = id;
    }
    public long getInternalId(){
        return internalId;
    }

    public long getProfile() {













































































































































































































































































































































































































        return profile;
    }

    public void setProfile(long profile) {
        this.profile = profile;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    /**
     * Returns the poi_id.
     *
     * @return poi_id
     */
    public long getUser_id() {
        return user_id;
    }

    /**
     * Sets the poi_id
     *
     * @param user_id set poi_id to set
     */
    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    /**
     * Returns the nickname.
     *
     * @return nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Sets the nickname
     *
     * @param nickname nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * Returns the mailadress.
     *
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the mailadress
     *
     * @param email mailadress to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the password.
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     *
     * @param password password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
