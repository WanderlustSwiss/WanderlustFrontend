package eu.wise_iot.wanderlust.model.DatabaseModel;

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
    long deviceId;
    String identifier;
    int language;
    int searchFilter;

    /**
     * Constructor.
     *
     * @param deviceId (required) device_id which is needed for saving into the database. Should be a long.
     * @param identifier (required) nickname of the user
     * @param language (required) mail of the user
     * @param searchFilter (required) password of the user
     */
    public Device(long deviceId, String identifier, int language, int searchFilter) {
        this.deviceId = deviceId;
        this.identifier = identifier;
        this.language = language;
        this.searchFilter = searchFilter;
    }

    /**
     * Returns the deviceId.
     *
     * @return deviceId
     */
    public long getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the deviceId
     *
     * @param deviceId set deviceId to set
     */
    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
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

    /**
     * Returns the language.
     *
     * @return language
     */
    public int getLanguage() {
        return language;
    }

    /**
     * Sets the language
     *
     * @param language set language to set
     */
    public void setLanguage(int language) {
        this.language = language;
    }

    /**
     * Returns the searchFilter.
     *
     * @return searchFilter
     */
    public int getSearchFilter() {
        return searchFilter;
    }

    /**
     * Sets the searchFilter
     *
     * @param searchFilter set id to set
     */
    public void setSearchFilter(int searchFilter) {
        this.searchFilter = searchFilter;
    }
}
