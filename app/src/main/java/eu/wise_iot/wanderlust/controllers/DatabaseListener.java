package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.controllers.DatabaseEvent;

public interface DatabaseListener {

    void update(DatabaseEvent event);
}
