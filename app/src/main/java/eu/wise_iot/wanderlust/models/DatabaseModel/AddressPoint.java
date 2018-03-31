package eu.wise_iot.wanderlust.models.DatabaseModel;

/**
 * AddressPoint
 *
 * Address from coordinates
 */
public class AddressPoint {

    private String name;
    private String city;


    public AddressPoint(String name, String city) {
        this.name = name;
        this.city = city;
    }
    public AddressPoint(){

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
