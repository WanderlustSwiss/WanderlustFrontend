//package eu.wise_iot.wanderlust;
//
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import eu.wise_iot.wanderlust.controllers.ProfileController;
//import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
//import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
//import eu.wise_iot.wanderlust.views.MainActivity;
//
//import static org.junit.Assert.*;
//
//
///**
// * Fragment which represents the UI of the profile of a user.
// *
// * @author Baris Demirci
// * @license MIT
// */
//@RunWith(AndroidJUnit4.class)
//public class ProfileControllerTest {
//
//    @Rule
//    public ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
//
//    private ProfileController profileController;
//    private UserDao userDao;
//    private ProfileDao profileDao;
//
//    @Before
//    public void setUp(){
//
//        profileController = new ProfileController();
//        userDao = UserDao.getDialog();
//        profileDao = ProfileDao.getDialog();
//
//    }
//
//    @Test
//    public void profileExists(){
//        assertTrue(profileController.profileExists());
//    }
//
//    @Test
//    public void getNickName(){
//        assertEquals("testbaris", profileController.getNickName());
//
//        userDao.removeAll();
//        assertEquals("no user", profileController.getNickName());
//    }
///*
//    @Test
//    public void getScore(){
//        assertEquals(0, profileController.getScore());
//
//        profileDao.removeAll();
//        assertEquals(0, profileController.getScore());
//    }
//
//    @Test
//    public void getAmountTours(){
//        assertEquals(0, profileController.getScore());
//    }*/
//
//    @Test
//    public void getAmountPoi(){
//        assertEquals(0, profileController.getAmountPoi());
//    }
//
//    @Test
//    public void getBirthDate(){
//        assertEquals("", profileController.getBirthDate());
//
//        profileDao.removeAll();
//        assertEquals("", profileController.getBirthDate());
//    }
//
//    @Ignore
//    @Test
//    public void getProfilePicture() {
//    }
//
//    @Ignore
//    @Test
//    public void getTours() {
//    }
//
//    @Ignore
//    @Test
//    public void getFavorites() {
//    }
//
//    @Ignore
//    @Test
//    public void getPois() {
//    }
//
//    @Ignore
//    @Test
//    public void getSavedTours() {
//    }
//
//}