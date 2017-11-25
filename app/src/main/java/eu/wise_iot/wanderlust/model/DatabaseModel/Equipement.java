package eu.wise_iot.wanderlust.model.DatabaseModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Equipement
 * @author Rilind Gashi
 * @license MIT
 */

@Entity
public class Equipement extends AbstractModel{

    @Id
    long equipement_id;
    String name;
    String description;

    public Equipement(long equipement_id, String name, String description) {
        this.equipement_id = equipement_id;
        this.name = name;
        this.description = description;
    }

    public long getEquipement_id() {
        return equipement_id;
    }

    public void setEquipement_id(long equipement_id) {
        this.equipement_id = equipement_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
