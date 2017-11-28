package eu.wise_iot.wanderlust.models.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Device
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Device extends AbstractModel{

    @Id
    long device_id;
    String identifier;

    /**
     * Constructor.
     *
     * @param device_id (required) device_id which is needed for saving into the database. Should be a long.
     * @param identifier (required) nickname of the user
     * @param language (required) email of the user
     * @param searchFilter (required) password of the user
     */
    public Device(long device_id, String identifier, int language, int searchFilter) {
        this.device_id = device_id;
        this.identifier = identifier;
    }

    /**
     * Returns the device_id.
     *
     * @return device_id
     */
    public long getDevice_id() {
        return device_id;
    }

    /**
     * Sets the device_id
     *
     * @param device_id set device_id to set
     */
    public void setDevice_id(long device_id) {
        this.device_id = device_id;
    }

    /**
     * Returns the identifier.
     *
     * @return identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier
     *
     * @param identifier set identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
