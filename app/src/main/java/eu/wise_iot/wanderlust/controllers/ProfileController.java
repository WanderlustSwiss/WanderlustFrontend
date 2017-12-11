package eu.wise_iot.wanderlust.controllers;

import android.app.Fragment;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.ProfileFragment;

public class ProfileController {

    private ProfileFragment profileFragment;

    private ProfileDao profileDao;
    private UserDao userDao;
    private UserTourDao tourDao;
    private PoiDao poiDao;

    public ProfileController(ProfileFragment fragment){
        profileFragment = fragment;
        userDao = new UserDao();
        profileDao = new ProfileDao();
        //tourDao = new UserTourDao(MainActivity.boxStore);
    }

    public String getNickName(){
        return userDao.find().get(0).getNickname();
    }

    public int getScore(){
        return profileDao.find().get(0).getScore();
    }

    public long getAmountTours(){
        return tourDao.count();
    }

    public long getAmountPoi(){
        return poiDao.count();
    }

    public String getProfilePicture(){
        return null;
    }

    public String getBirthDate(){
        return profileDao.find().get(0).getBirthday();
    }

    public List getTours(){
        return null;
    }

    public List getFavorites(){
        return null;
    }

    public List getPois(){
        return null;
    }

    public List getSavedTours(){
        return null;
    }


}
