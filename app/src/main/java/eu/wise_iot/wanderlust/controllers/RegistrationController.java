package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.views.RegistrationFragment;

/**
 * Created by Joshi on 28.11.2017.
 */

public class RegistrationController{
    private RegistrationFragment registrationFragment;

    public RegistrationController(RegistrationFragment fragment){
        this.registrationFragment = fragment;
    }

    public void registerUser(User user, FragmentHandler handler){
        // todo
    }

}
