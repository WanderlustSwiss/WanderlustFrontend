package eu.wise_iot.wanderlust.models.DatabaseModel;

/**
 * AddressPoint
 * <p>
 * Address from coordinates
 */
public class AddressPoint {

    private String name;
    private String city;
    private String state;
    private String road;
    private String village;


    public AddressPoint() {
    }

    public AddressPoint(String name, String city, String state, String road, String village) {
        this.name = name;
        this.city = city;
        this.state = state;
        this.road = road;
        this.village = village;
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


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getVillage() {
        return village;
    }

    public void setVillage(String village) {
        this.village = village;
    }
}
