package eu.wise_iot.wanderlust.controllers;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.Tour;
import eu.wise_iot.wanderlust.models.DatabaseModel.Trip;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.CommunityTourDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.FavoriteDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.TripDao;
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
    private TripDao tripDao;
    private PoiDao poiDao;
    private FavoriteDao favoriteDao;
    private CommunityTourDao communityTourDao;
    private DifficultyTypeDao difficultyTypeDao;

    public ProfileController() {
        profileDao = ProfileDao.getInstance();
        userDao = UserDao.getInstance();
        userTourDao = UserTourDao.getInstance();
        tripDao = TripDao.getInstance();
        poiDao = PoiDao.getInstance();
        difficultyTypeDao = DifficultyTypeDao.getInstance();
        favoriteDao = FavoriteDao.getInstance();
        communityTourDao = CommunityTourDao.getInstance();
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
        return tripDao.count();
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
    public String getProfilePicture() {

        //TODO: next release
        //TODO: Ask tru for image handling.
        //Backend: GET Request /profile/img
        return null;
    }

    public void setProfilePicture(String path){
        //TODO: next release
        //TODO: Ask tru for image handling.
        // Backend: Delete image with DELETE Request to /profile/img then
        // POST Reuqest /profile/img with image
    }

    public void deleteProfilePicture(){
        //TODO: next release
        //TODO: Ask tru for image handling.
        //DELETE Request to /profile/img
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
     * Gets the list with all favorite tours of logged in user
     *
     * @param handler list and response given back to handler
     */
    public void getFavorites(FragmentHandler handler) {
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

    /**
     * Gets the list with all user tours of logged in user
     *
     * @return list with all trips
     */
    public List<Trip> getTrips(){
        return tripDao.find();
    }

    /**
     * Gives back the tour object to a trip.
     *
     * @param trip trip whose tour is requested
     * @param handler tour and response given back to handler
     */
    public void getTourToTrip(Trip trip, FragmentHandler handler) {
        userTourDao.retrieve(trip.getTour(), handler);

    }

    /**
     * Deletes a trip from the database
     *
     * @param tour to delete
     * @param handler defines further action
     */
    public void deleteTrip(Tour tour, FragmentHandler handler){
        tripDao.delete(tour, handler);
    }

    /**
     * Deletes a communit tour from the local database
     *
     * @param communityTour to delete
     */
    public void deleteCommunityTour(Tour communityTour){
        communityTourDao.delete(communityTour);
    }

}
