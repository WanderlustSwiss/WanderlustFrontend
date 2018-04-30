package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;

/**
 * Registration Controller which handles registrations of the user
 *
 * @author Joshua Meier
 * @license MIT
 */
public class RegistrationController {

    private final UserDao userDao;

    /**
     * Create a registration contoller
     */
    public RegistrationController() {
        userDao = UserDao.getInstance();
    }

    /**
     * @param user
     * @param handler Creates a user Dao and starts the saving process of an user
     */
    public void registerUser(User user, FragmentHandler handler) {
        userDao.create(user, handler);
    }

}
