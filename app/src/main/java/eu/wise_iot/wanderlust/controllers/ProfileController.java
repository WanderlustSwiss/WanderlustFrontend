package eu.wise_iot.wanderlust.controllers;

import android.app.Fragment;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.ProfileFragment;

/**
 * Created by Ali Laptop on 28.11.2017.
 */

public class ProfileController {

    private ProfileFragment profileFragment;
    private UserDao userDao;
    private ProfileDao profileDao;

    public ProfileController(ProfileFragment fragment){
        profileFragment = fragment;
        userDao = new UserDao(MainActivity.boxStore);

    }

    public String getNickName(){
        return userDao.getUser().getNickname();
    }

    public int getScore(){
        return profileDao.find().get(0).getScore();
    }

    public long getCreatedRoutes(){
        //TODO::
        return 0;
    }

    public List getAllTours(){
        //TODO::
        return null;
    }

    public List getPOIs(){
        //TODO::
        return null;
    }

    public String getProfilePicture(){
        //TODO::
        return null;
    }




}
