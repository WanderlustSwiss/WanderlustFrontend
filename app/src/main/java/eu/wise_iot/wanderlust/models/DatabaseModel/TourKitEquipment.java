package eu.wise_iot.wanderlust.models.DatabaseModel;

/**
 * Helper object for backend communication
 *
 * @author Tobias Rüegsegger
 * @license MIT
 */
public class TourKitEquipment {

    private final Equipment equipment;

    public TourKitEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public Equipment getEquipment() {
        return equipment;
    }

}
