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
    long equipementId;
    String name;
    String description;

    public Equipement(long equipementId, String name, String description) {
        this.equipementId = equipementId;
        this.name = name;
        this.description = description;
    }

    public long getEquipementId() {
        return equipementId;
    }

    public void setEquipementId(long equipementId) {
        this.equipementId = equipementId;
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
