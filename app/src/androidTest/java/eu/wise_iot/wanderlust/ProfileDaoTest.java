//package eu.wise_iot.wanderlust;
//
//import android.content.Context;
//import android.support.test.InstrumentationRegistry;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.junit.Assert.*;
//
//import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
//import eu.wise_iot.wanderlust.models.DatabaseModel.Profile_;
//import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
//import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
//import io.objectbox.Box;
//import io.objectbox.BoxStore;
//import io.objectbox.query.QueryBuilder;
//
//
///**
// * @author Rilind Gashi
// */
//
//@RunWith(AndroidJUnit4.class)
//public class ProfileDaoTest {
//
//    BoxStore boxStore;
//    Box<Profile> profileBox;
//    QueryBuilder<Profile> profileQueryBuilder;
//    Profile testProfile;
//    ProfileDao profileDao;
//
//    @Before
//    public void setUpBefore(){
//        Context appContext = InstrumentationRegistry.getTargetContext();
//        boxStore = MyObjectBox.builder().androidContext(appContext).build();
//        profileDao = new ProfileDao(boxStore);
//        profileBox = boxStore.boxFor(Profile.class);
//        profileQueryBuilder = profileBox.query();
//        testProfile = new Profile(0, "13.09.1995", 1, "picturePath");
//    }
//
//    @Test
//    public void createTest(){
//        profileDao.create(testProfile);
//        assertEquals("13.09.1995", profileQueryBuilder.equal(Profile_.birthdate, "13.09.1995")
//                .build().findFirst().getBirthday());
//    }
//
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
