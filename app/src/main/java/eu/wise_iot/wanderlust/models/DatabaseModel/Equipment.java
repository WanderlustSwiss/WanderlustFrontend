package eu.wise_iot.wanderlust.models.DatabaseModel;

import java.io.Serializable;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Equipment
 *
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Equipment extends AbstractModel implements Serializable {

    @Id
    long internal_id;
    long equip_id;
    int type;
    String name;
    String description;
    String adLink;
    byte[] seasons;
    byte[] weather;
    int minTemperature;
    int maxTemperature;
    boolean isBasic;

    long routeKit;

    @Convert(converter = Profile.imageInfoConverter.class, dbType = String.class)
    ImageInfo imagePath;

    public Equipment(){
        this.internal_id = 0;
        this.equip_id = 0;
        this.type = 0;
        this.name = "init";
        this.description = "init";
        this.seasons = new byte[0];
        this.weather = new byte[0];
        this.minTemperature = 0;
        this.maxTemperature = 0;
        this.isBasic = false;
        this.routeKit = 0;
    }

    public Equipment(long internal_id, long equip_id, int type, String name, String description, byte[] seasons, byte[] weather, int minTemperature, int maxTemperature, boolean isBasic, long routeKit) {
        this.internal_id = internal_id;
        this.equip_id = equip_id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.seasons = seasons;
        this.weather = weather;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.isBasic = isBasic;
        this.routeKit = routeKit;
    }

    public ImageInfo getImagePath() {
        return imagePath;
    }

    public ImageInfo getImageById(long id){
        if(imagePath.getId() == id){
            return imagePath;
        }
        return null;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public void setEquip_id(long equip_id) {
        this.equip_id = equip_id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAdLink(String adLink) {
        this.adLink = adLink;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSeasons(byte[] seasons) {
        this.seasons = seasons;
    }

    public void setWeather(byte[] weather) {
        this.weather = weather;
    }

    public void setMinTemperature(int minTemperature) {
        this.minTemperature = minTemperature;
    }

    public void setMaxTemperature(int maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public void setBasic(boolean basic) {
        isBasic = basic;
    }

    public void setRouteKit(long routeKit) {
        this.routeKit = routeKit;
    }

    public long getInternal_id() {
        return internal_id;
    }

    public long getEquip_id() {
        return equip_id;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getAdLink() {
        return adLink;
    }

    public String getDescription() {
        return description;
    }

    public byte[] getSeasons() {
        return seasons;
    }

    public byte[] getWeather() {
        return weather;
    }

    public int getMinTemperature() {
        return minTemperature;
    }

    public int getMaxTemperature() {
        return maxTemperature;
    }

    public boolean isBasic() {
        return isBasic;
    }

    public long getRouteKit() {
        return routeKit;
    }

    @Override
    public String toString() { return name; }
}
