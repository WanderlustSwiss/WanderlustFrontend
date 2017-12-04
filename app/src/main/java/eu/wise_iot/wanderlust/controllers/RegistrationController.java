package eu.wise_iot.wanderlust.controllers;

import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.RegistrationFragment;

/**
 * @author Yoshi
 */
public class RegistrationController{

    public RegistrationController(){}

    public void registerUser(User user, FragmentHandler handler){
        DatabaseController.userDao.create(user, handler);
    }

}
