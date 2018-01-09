package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Test class for Profile dao
 * @author Rilind Gashi, Alexander Weinbeck
 */
//public class ProfileDaoTest {

//    BoxStore boxStore;
//    Box<Profile> profileBox;
//    QueryBuilder<Profile> profileQueryBuilder;
//    Profile testProfile;
//    ProfileDao profileDao;
//
//    private final static int PROFILE_TEST_ID = 999;
//    private static final byte PROFILE_TEST_IMAGEID = 9;
//
//
//    @Before
//    public void setUpBefore() {
//
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        boxStore = MyObjectBox.builder().androidContext(appContext).build();
//        LoginService loginService = ServiceGenerator.createService(LoginService.class);
//        LoginUser testUser = new LoginUser("testuser", "Ha11loW4lt");
//
//        Call<User> call = loginService.basicLogin(testUser);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful()) {
//                    Headers headerResponse = response.headers();
//                    //convert header to Map
//                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
//                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));
//
//                } else fail();
//            }
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                fail();
//            }
//        });
//
//        //profileDao = new ProfileDao(boxStore, appContext);
//        profileBox = boxStore.boxFor(Profile.class);
//        profileQueryBuilder = profileBox.query();
//        //testProfile
//
//        //Profile(long internal_id, long profile_id, byte imageId, int score, String birthday, String language, long user, long difficulty)
//        testProfile = new Profile(99, 99, PROFILE_TEST_IMAGEID, 55, "01.01.1999", "EN", 999, 9);
//    }
//
//    @After
//    public void setUpAfter() {
//        //close boxstore
//        if(boxStore != null) boxStore.close();
//    }
//
//    /**
//     * test to update profile remote
//     */
//    @Test
//    public void updateTest(){
//        try {
//            CountDownLatch doneSignal = new CountDownLatch(1);
//
//            ProfileDao profileDao = new ProfileDao();
//            assertTrue(false);
//            profileDao.update(testProfile, new FragmentHandler() {
//                @Override
//                public void onResponse(ControllerEvent controllerEvent) {
//                    switch (controllerEvent.getType()){
//                        case OK:
//                            doneSignal.countDown();
//                            break;
//                        default:
//                            fail();
//                    }
//                }
//            });
//
//            //wait for async method
//            doneSignal.await();
//            //continue if arrived
//            //given testProfile from Remote should be in Local database
//            assertTrue(profileBox.getAll().contains(testProfile));
//
//        } catch (Exception e){
//            fail(e.getMessage());
//        }
//    }
//    /**
//     * test to retrieve profile remote
//     */
//    @Test
//    public void retrieveTest(){
//
//        try {
//            ProfileDao profileDao = new ProfileDao();
//            Profile testProfileUpdate = new Profile();
//
//            CountDownLatch doneSignal = new CountDownLatch(1);
//
//            profileDao.retrieve(testProfile, new FragmentHandler() {
//                @Override
//                public void onResponse(ControllerEvent controllerEvent) {
//                    switch (controllerEvent.getType()){
//                        case OK:
//                            doneSignal.countDown();
//                            break;
//                        default:
//                            fail();
//                    }
//                }
//            });
//            //wait for async method
//            doneSignal.await();
//            //continue if arrived
//            //given testProfile from Remote should be in Local database
//            assertTrue(profileBox.getAll().contains(testProfileUpdate));
//
//        } catch (Exception e){
//            fail(e.getMessage());
//        }
//    }
//    /**
//     * test to add image remote
//     */
//    @Test
//    public void addImageTest(){
//        try {
//            ProfileDao profileDao = new ProfileDao();
//
//            CountDownLatch doneSignal = new CountDownLatch(1);
//            profileDao.addImage(new File("testString"), testProfile, new FragmentHandler() {
//                @Override
//                public void onResponse(ControllerEvent controllerEvent) {
//                    switch (controllerEvent.getType()){
//                        case OK:
//                            doneSignal.countDown();
//                            break;
//                        default:
//                            fail();
//                    }
//                }
//            });
//
//            //wait for async method
//            doneSignal.await();
//            //continue if arrived
//            //given changed profile should be in Local database because of changes
//            assertTrue(profileBox.getAll().contains(testProfile));
//            //there should be an image now
//            assertTrue(profileBox.getAll().get(PROFILE_TEST_ID).getImageById(PROFILE_TEST_IMAGEID).equals(testProfile.getImageId()));
//        } catch (Exception e){
//            fail(e.getMessage());
//        }
//    }
//    /**
//     * test to delete image remote
//     */
//    @Test
//    public void deleteImageTest(){
//        try {
//            ProfileDao profileDao = new ProfileDao();
//
//            CountDownLatch doneSignal = new CountDownLatch(1);
//            profileDao.deleteImage(PROFILE_TEST_ID,PROFILE_TEST_IMAGEID, new FragmentHandler() {
//                @Override
//                public void onResponse(ControllerEvent controllerEvent) {
//                    switch (controllerEvent.getType()){
//                        case OK:
//                            doneSignal.countDown();
//                            break;
//                        default:
//                            fail();
//                    }
//                }
//            });
//
//            //wait for async method
//            doneSignal.await();
//            //continue if arrived
//            //given changed profile should not be in Local database because of changes
//            assertNull(profileBox.getAll().get(PROFILE_TEST_ID).getImageById(PROFILE_TEST_IMAGEID));
//        } catch (Exception e){
//            fail(e.getMessage());
//        }
//    }
//
//}


//    @Test
//    public void createTest() {
//        profileDao.create(testProfile);
//        assertEquals("13.09.1995", profileQueryBuilder.equal(Profile_.birthdate, "13.09.1995")
//                .build().findFirst().getBirthday());
//    }

//    @Test
//    public void updateTest(){
//        profileBox.put(testProfile);
//        testProfile.setBirthday("13.09.2017");
//        profileBox.put(testProfile);
//        assertEquals("13.09.2017", profileQueryBuilder.
//                equal(Profile_.profileId, testProfile.getProfile_id()).build().findFirst().getBirthday());
//    }
//
//    @Test
//    public void findOneTest(){
//        profileBox.put(testProfile);
//        try {
//            assertEquals("13.09.1995", profileDao.findOne("birthdate", "13.09.1995").getBirthday());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void findTest(){
//        Profile profileOne = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileTwo = new Profile(0, "11.09.1995", 1, "picturePath");
//
//        profileBox.put(profileOne);
//        profileBox.put(profileTwo);
//
//        assertEquals(2, profileDao.find().size());
//    }
//
//    @Test
//    public void findDetailedTest(){
//        Profile profileOne = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileTwo = new Profile(0, "11.09.1995", 1, "picturePath");
//        Profile profileThree = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileFour = new Profile(0, "23.09.1995", 1, "picturePath");
//
//        profileBox.put(profileOne);
//        profileBox.put(profileTwo);
//        profileBox.put(profileThree);
//        profileBox.put(profileFour);
//
//        try {
//            assertEquals(2, profileDao.find("birthdate", "13.09.1995").size());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void countTest(){
//        Profile profileOne = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileTwo = new Profile(0, "11.09.1995", 1, "picturePath");
//        Profile profileThree = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileFour = new Profile(0, "23.09.1995", 1, "picturePath");
//
//        profileBox.put(profileOne);
//        profileBox.put(profileTwo);
//        profileBox.put(profileThree);
//        profileBox.put(profileFour);
//
//        assertEquals(4, profileDao.count());
//    }
//
//    @Test
//    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
//        Profile profileOne = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileTwo = new Profile(0, "11.09.1995", 1, "picturePath");
//        Profile profileThree = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileFour = new Profile(0, "23.09.1995", 1, "picturePath");
//
//        profileBox.put(profileOne);
//        profileBox.put(profileTwo);
//        profileBox.put(profileThree);
//        profileBox.put(profileFour);
//
//        assertEquals(2, profileDao.count("birthdate", "13.09.1995"));
//    }
//
//    @Test
//    public void deleteTest(){
//        Profile profileOne = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileTwo = new Profile(0, "11.09.1995", 1, "picturePath");
//
//        profileBox.put(profileOne);
//        profileBox.put(profileTwo);
//
//        try {
//            profileDao.delete("birthdate", "13.09.1995");
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//
//        assertEquals(1, profileDao.find().size());
//    }
//
//    @Test
//    public void deleteAllTest(){
//        Profile profileOne = new Profile(0, "13.09.1995", 1, "picturePath");
//        Profile profileTwo = new Profile(0, "11.09.1995", 1, "picturePath");
//
//        profileBox.put(profileOne);
//        profileBox.put(profileTwo);
//
//        profileDao.deleteAll();
//        assertEquals(0, profileDao.count());
//    }
//
//    @After
//    public void after() {
//        profileBox.removeAll();
//        boxStore.close();
//    }
//}
