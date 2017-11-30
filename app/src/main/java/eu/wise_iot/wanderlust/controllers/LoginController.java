package eu.wise_iot.wanderlust.controllers;

import org.apache.commons.lang3.NotImplementedException;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.views.LoginFragment;

/**
 * Created by Joshi on 30.11.2017.
 */

public class LoginController {
    private LoginFragment loginFragment;

    public LoginController(LoginFragment fragment){
        this.loginFragment = fragment;
    }

    public void logIn(LoginUser user, FragmentHandler handler){
        // Todo
        throw new NotImplementedException("Not Implemented :(");

    }

}
