package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.AbstractModel;

/*
 * FragmentHandler for fragments to handle backend requests
 * @author Tobias Rüegsegger
 * @license MIT
 */
public interface FragmentHandler {
    void onResponse(Event event, AbstractModel model);
}
