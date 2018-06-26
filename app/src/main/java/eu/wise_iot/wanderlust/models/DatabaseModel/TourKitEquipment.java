package eu.wise_iot.wanderlust.models.DatabaseModel;

/**
 * Helper object for backend communication
 *
 * @author Tobias Rüegsegger
 * @license GPL-3.0
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
