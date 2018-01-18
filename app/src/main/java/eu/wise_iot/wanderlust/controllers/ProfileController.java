package eu.wise_iot.wanderlust.controllers;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.CommunityTour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.UserTour;
import eu.wise_iot.wanderlust.models.Old.Tour;

/**
 * Profile controller which initializes the profile view
 * and all the lists of the user
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileController {


    public ProfileController() {

    }

    /**
     * Indicates if a profile exists
     *
     * @return true if profile exists
     */
    public boolean profileExists() {
        List<Profile> list = DatabaseController.profileDao.find();
        if (list == null || list.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the nickname of logged in user
     *
     * @return the nickname of the user
     */
    public String getNickName() {
        List<User> list = DatabaseController.userDao.find();
        if (list == null || list.size() == 0) {
            return "no user";
        }
        return DatabaseController.userDao.find().get(0).getNickname();
    }

    public String getEmail(){
        List<User> list = DatabaseController.userDao.find();
        if(list == null || list.isEmpty()){
          return "";
        }
        return DatabaseController.userDao.find().get(0).getEmail();
    }

    public void setEmail(String email, Context context, FragmentHandler fragmentHandler){
        if(email == ""){
            Toast.makeText(context, "E-Mail darf nicht leer sein.", Toast.LENGTH_SHORT).show();
        }else{
            User user = DatabaseController.userDao.getUser();
            user.setEmail(email);

            DatabaseController.userDao.update(user, fragmentHandler);
        }

    }

    /**
     * Gets the score of logged in user
     *
     * @return the score of the user
     */
    public int getScore() {
        List<Profile> list = DatabaseController.profileDao.find();
        if (list == null || list.size() == 0) {
            return 0;
        }
        return DatabaseController.profileDao.find().get(0).getScore();
    }

    /**
     * Gets amount of user tours of logged in user
     *
     * @return the amount of user tours
     */
    public long getAmountTours() {
        List<UserTour> list = DatabaseController.userTourDao.find();
        if (list == null) {
            return 0;
        }
        return DatabaseController.userTourDao.count();
    }

    /**
     * Gets the amount of poi's of logged in user
     *
     * @return the amount of poi's
     */
    public long getAmountPoi() {
        List<Poi> list = DatabaseController.poiDao.find();
        if (list == null || list.size() == 0) {
            return 0;
        }
        return DatabaseController.poiDao.count();
    }

    public long getDifficulty(){
        DatabaseController.difficultyTypeDao.sync();
        long difficulty = DatabaseController.profileDao.find().get(0).getDifficulty();

        return difficulty;
    }

    public void setDifficulty(long difficulty, Context context, FragmentHandler fragmentHandler){
        if(difficulty < 1 || difficulty > 6){
            Toast.makeText(context, "Dieses Level gibt es nicht.", Toast.LENGTH_SHORT).show();
        }else{
            Profile profile = DatabaseController.profileDao.find().get(0);
            profile.setDifficulty(difficulty);
            DatabaseController.profileDao.upd





















            ate(profile, fragmentHandler);
        }
    }

    /**
     * Gets the profile picture of logged in user
     *
     * @return the path to the profile picture
     */
    public String getProfilePicture() {

        //TODO: next release
        return null;
    }

    public void setProfilePicture(String path){

    }

    /**
     * Gets the birthdate of logged in user
     *
     * @return the birthdate of the user
     */
    public String getBirthDate() {
        List<Profile> list = DatabaseController.profileDao.find();
        if (list == null || list.size() == 0) {
            return "";
        }
        return DatabaseController.profileDao.find().get(0).getBirthday();
    }

    /**
     * Gets the list with all user tours of logged in user
     *
     * @return list with user tours
     */
    public List<Tour> getTours() {
        //TODO: for next release
        return null;
    }

    /**
     * Gets the list with all favorites of logged in user
     *
     * @return list with favorites
     */
    public List getFavorites() {
        //TODO: for next release
        return null;
    }

    /**
     * Gets the list with all poi's of logged in user
     *
     * @return list with poi's
     */
    public List<Poi> getPois() {
        //TODO: for next release
        return null;
    }

    /**
     * Gets the list with all saved tours of logged in user
     *
     * @return list with all saved tours
     */
    public List<CommunityTour> getSavedTours() {
        //TODO: for next release
        return null;
    }


}
