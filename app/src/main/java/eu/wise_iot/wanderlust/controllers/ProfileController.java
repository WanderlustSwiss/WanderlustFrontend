package eu.wise_iot.wanderlust.controllers;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Favorite;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseObject.CommunityTourDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;

/**
 * Profile controller which initializes the profile view
 * and all the lists of the user
 *
 * @author Baris Demirci
 * @license MIT
 */
public class ProfileController {

    private ProfileDao profileDao;
    private UserDao userDao;
    private UserTourDao userTourDao;
    private PoiDao poiDao;
    private FavoriteDao favoriteDao;
    private CommunityTourDao communityTourDao;
    private DifficultyTypeDao difficultyTypeDao;
    private ImageController imageController;
    private Context context;

    public ProfileController() {
        profileDao = ProfileDao.getInstance();
        userDao = UserDao.getInstance();
        userTourDao = UserTourDao.getInstance();
        poiDao = PoiDao.getInstance();
        difficultyTypeDao = DifficultyTypeDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        communityTourDao = CommunityTourDao.getInstance();
        imageController = ImageController.getInstance();
        context = DatabaseController.getMainContext();
        difficultyTypeDao.retrive();
    }

    /**
     * Indicates if a profile exists
     *
     * @return true if profile exists
     */
    public boolean profileExists() {
        return profileDao.getProfile() != null;
    }

    /**
     * Gets the nickname of logged in user
     *
     * @return the nickname of the user
     */
    public String getNickName() {
        User user = userDao.getUser();
        if (user == null || user.getNickname() == "") {
            return "no user";
        }
        return user.getNickname();
    }

    public String getEmail(){
        User user = userDao.getUser();
        if(user == null || user.getEmail() == ""){
          return "";
        }
        return user.getEmail();
    }

    public void setEmail(String email, Context context, FragmentHandler fragmentHandler){
        if(email == ""){
            Toast.makeText(context, "E-Mail darf nicht leer sein.", Toast.LENGTH_SHORT).show();
        }else{
            User user = userDao.getUser();
            user.setEmail(email);
            userDao.update(user, fragmentHandler);
        }

    }

    /**
     * Gets the score of logged in user
     *
     * @return the score of the user
     */
    public int getScore() {
        Profile profile = profileDao.getProfile();
        if (profile == null) {
            return 0;
        }
        return profile.getScore();
    }

    /**
     * Gets the sex of logged in user
     *
     * @return the sex of the user
     */
    public int getSex() {
        Profile profile = profileDao.getProfile();
        if (profile == null) {
            return 0;
        }
        return profile.getSex();
    }

    /**
     * Gets amount of user tours of logged in user
     *
     * @return the amount of user tours
     */
    public long getAmountTours() {
        return userTourDao.count();
    }

    /**
     * Gets the amount of poi's of logged in user
     *
     * @return the amount of poi's
     */
    public long getAmountPoi() {
        return poiDao.count();
    }

    public long getDifficulty(){
        return profileDao.getProfile().getDifficulty();
    }

    public void setDifficulty(long difficulty, Context context, FragmentHandler fragmentHandler){
        if(difficulty < 1 || difficulty > 6){
            Toast.makeText(context, "Dieses Level gibt es nicht.", Toast.LENGTH_SHORT).show();
        }else{
            Profile profile = profileDao.getProfile();
            profile.setDifficulty(difficulty);
            profileDao.update(profile, fragmentHandler);
        }
    }

    /**
     * Gets the profile picture of logged in user
     *
     * @return the path to the profile picture
     */
    public File getProfilePicture() {
        Profile profile = profileDao.getProfile();
        if (profile.getImagePath() != null){
            return imageController.getImage(profile.getImagePath());
        }
        return null;
    }
    /**
     * Set profile image
     *
     * @param handler
     * @param srcBmp
     */
    public void setProfilePicture(Bitmap srcBmp, FragmentHandler handler){
        Profile profile = profileDao.getProfile();
        Bitmap mutableDstBmp = srcBmp;

        File image = new File(context.getCacheDir(), "profile_image.jpg");
        OutputStream os;
        try {
            os = new BufferedOutputStream(new FileOutputStream(image));
            mutableDstBmp.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.close();
            profileDao.addImage(image, profile, handler);
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Delete profile image
     *
     * @param handler
     */
    public void deleteProfilePicture(FragmentHandler handler){
        //Todo: Wrire Fragment hanlder
        Profile profile = profileDao.getProfile();
        if (profile.getImagePath() != null){
            profileDao.deleteImage(handler);
        }
    }

    /**
     * Gets the birthdate of logged in user
     *
     * @return the birthdate of the user
     */
    public String getBirthDate() {
        Profile profile = profileDao.getProfile();
        if (profile == null) {
            return "";
        }
        return profile.getBirthday();
    }

    /**
     * Gets the list with all user tours of logged in user
     *
     * @return list with user tours
     */
    public List<Tour> getTours() {
        return userTourDao.find();
    }

    /**
     * Gets the list with all favorite tours of logged in user
     *
     * @return list with tours
     */
    public void getFavorites(FragmentHandler handler) {
        //Todo: Write handler in the adapter/fragment
        favoriteDao.retrievAllFavoriteTours(handler);
    }

    /**
     * Gets the list with all poi's of logged in user
     *
     * @return list with poi's
     */
    public List<Poi> getPois() {
        return poiDao.find();
    }

    /**
     * Gets the list with all saved tours of logged in user
     *
     * @return list with all saved tours
     */
    public List<Tour> getSavedTours() {
        return communityTourDao.find();
    }


}
