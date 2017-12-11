package eu.wise_iot.wanderlust.controllers;

import org.apache.commons.lang3.NotImplementedException;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.views.LoginFragment;

/*
 * Login Controller which handles the login of the user
 * @author Joshua
 * @license MIT
 */
public class LoginController {
    private LoginFragment loginFragment;

    /**
     * Create a login contoller
     */
    public LoginController(LoginFragment fragment){
        this.loginFragment = fragment;
    }

    public void logIn(LoginUser user, FragmentHandler handler){
        // Todo
        throw new NotImplementedException("Not Implemented :(");

    }

}
