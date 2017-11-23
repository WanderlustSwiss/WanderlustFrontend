package eu.wise_iot.wanderlust.model.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * User
 * @author Rilind Gashi
 * @license MIT <license in our case always MIT>
 */

@Entity
public class User extends AbstractModel{

    @Id
    long id;

    String nickname;
    String mail;
    String password;

    /**
     * Constructor.
     *
     * @param id (required) userid which is needed for saving into the database. Should be a long.
     * @param nickname (required) nickname of the user
     * @param mail (required) mail of the user
     * @param password (required) password of the user
     */

    public User(long id, String nickname, String mail, String password) {
        this.id = id;
        this.nickname = nickname;
        this.mail = mail;
        this.password = password;
    }

    public long getId() { return id; }

    /**
     * Sets the id
     *
     * @param id set id to set
     */
    public void setId(long id) {
        this.id = id;
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
     * @return mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * Sets the mailadress
     *
     * @param mail mailadress to set
     */
    public void setMail(String mail) {
        this.mail = mail;
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
