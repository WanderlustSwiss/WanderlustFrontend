package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.RegistrationFragment;

/*
 * Registration Controller which handles registrations of the user
 * @author Joshua
 * @license MIT
 */
public class RegistrationController{
    private RegistrationFragment registrationFragment;

    /**
     * Create a registration contoller
     */
    public RegistrationController(RegistrationFragment fragment){
        this.registrationFragment = fragment;
    }

    /**
     * @param user
     * @param handler
     * Creates a user Dao and starts the saving process of an user
     */
    public void registerUser(User user, FragmentHandler handler){
        UserDao newUser = new UserDao(MainActivity.boxStore);
        newUser.create(user, handler);
    }

}
