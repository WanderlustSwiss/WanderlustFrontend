package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.User;

/*
 * Registration Controller which handles registrations of the user
 * @author Joshua
 * @license MIT
 */
public class RegistrationController {

    /**
     * Create a registration contoller
     */
    public RegistrationController() {
    }

    /**
     * @param user
     * @param handler Creates a user Dao and starts the saving process of an user
     */
    public void registerUser(User user, FragmentHandler handler) {
        DatabaseController.userDao.create(user, handler);
    }

}
