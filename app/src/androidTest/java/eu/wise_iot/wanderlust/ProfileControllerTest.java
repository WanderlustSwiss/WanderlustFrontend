package eu.wise_iot.wanderlust;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.ProfileFragment;
import io.objectbox.BoxStore;

import static org.junit.Assert.*;


/**
 * Fragment which represents the UI of the profile of a user.
 *
 * @author Baris Demirci
 * @license MIT
 */
@RunWith(AndroidJUnit4.class)
public class ProfileControllerTest {

    @Rule
    public ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    ProfileController profileController;
    UserDao userDao;
    ProfileDao profileDao;

    @Before
    public void setUp(){

        profileController = new ProfileController();
        userDao = UserDao.getInstance();
        profileDao = ProfileDao.getInstance();

    }

    @Test
    public void profileExists(){
        assertTrue(profileController.profileExists());
    }

    @Test
    public void getNickName(){
        assertEquals("testbaris", profileController.getNickName());

        userDao.removeAll();
        assertEquals("no user", profileController.getNickName());
    }

    @Test
    public void getScore(){
        assertEquals(0, profileController.getScore());

        profileDao.removeAll();
        assertEquals(0, profileController.getScore());
    }

    @Test
    public void getAmountTours(){
        assertEquals(0, profileController.getScore());
    }

    @Test
    public void getAmountPoi(){
        assertEquals(0, profileController.getAmountPoi());
    }

    @Test
    public void getBirthDate(){
        assertEquals("", profileController.getBirthDate());

        profileDao.removeAll();
        assertEquals("", profileController.getBirthDate());
    }

    @Ignore
    @Test
    public void getProfilePicture() throws Exception {
    }

    @Ignore
    @Test
    public void getTours() throws Exception {
    }

    @Ignore
    @Test
    public void getFavorites() throws Exception {
    }

    @Ignore
    @Test
    public void getPois() throws Exception {
    }

    @Ignore
    @Test
    public void getSavedTours() throws Exception {
    }

}