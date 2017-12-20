package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.DifficultyType;
import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.DifficultyTypeDao;
import eu.wise_iot.wanderlust.services.LoginService;
import eu.wise_iot.wanderlust.services.ServiceGenerator;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Testing for DifficultyType DAO
 * @author Alexander Weinbeck
 * @license MIT
 */

public class DifficultTypeDaoTest {
//
    BoxStore boxStore;

    private Box<DifficultyType> difficultyTypeBox;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        LoginService loginService = ServiceGenerator.createService(LoginService.class);

        LoginUser testUser = new LoginUser("testuser", "Ha11loW4lt");

        Call<User> call = loginService.basicLogin(testUser);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Headers headerResponse = response.headers();
                    //convert header to Map
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));

                } else fail();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                fail();
            }
        });
    }
    /**
     * test to sync difficulty types with the database.
     */
    @Test
    public void syncTest(){
        try {
            //generate test obj
            DifficultyTypeDao dt = new DifficultyTypeDao();

            CountDownLatch doneSignal = new CountDownLatch(1);
            //perform sync
            dt.sync();
            //wait for async method
            doneSignal.await();
            //continue if arrived
            //every DifficultyType of Server should be in Local database
            for(DifficultyType dtIterate : difficultyTypeBox.getAll()){
                //compare here with actual database data
                //dtIterate.equals()
            }

        } catch (Exception e){
            fail();
        }
        //make required assertion for properties
//        assertEquals(userBox.get(1).getUser_id(),testUser.getUser_id());
//        assertEquals(userBox.get(1).getAccountType(), testUser.getAccountType());
//        assertEquals(userBox.get(1).isActive(),testUser.isActive());
//        assertEquals(userBox.get(1).getEmail(), testUser.getEmail());
//        assertEquals(userBox.get(1).getPassword(), testUser.getPassword());
//        assertEquals(userBox.get(1).getNickname(), testUser.getNickname());
    }


//
//    @Test
//    public void createTest(){
//        difficultTypeDao.create(testDifficulty);
//        assertEquals("T1", difficultyQueryBuilder.equal(DifficultType_.typename, "T1").build().findFirst().getName());
//    }
//
//    @Test
//    public void updateTest(){
//        difficultyBox.put(testDifficulty);
//        testDifficulty.setName("T2");
//        difficultyBox.put(testDifficulty);
//        assertEquals("T2", difficultyQueryBuilder.equal(DifficultType_.id, testDifficulty.getDifft_id()).build().findFirst().getName());
//    }
//
//    @Test
//    public void findOneTest(){
//        difficultyBox.put(testDifficulty);
//        try {
//            assertEquals("T1", difficultTypeDao.findOne("identifier", "deviceIdentifier").getName());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void findTest(){
//        DifficultType difficultyOne = new DifficultType(0, "T1");
//        DifficultType difficultyTwo = new DifficultType(0, "T2");
//
//        difficultyBox.put(difficultyOne);
//        difficultyBox.put(difficultyTwo);
//
//        assertEquals(2, difficultTypeDao.find().size());
//    }
//
//    @Test
//    public void findDetailedTest(){
//        DifficultType difficultyOne = new DifficultType(0, "T1");
//        DifficultType difficultyTwo = new DifficultType(0, "T2");
//        DifficultType difficultyThree = new DifficultType(0, "T3");
//        DifficultType difficultyFour = new DifficultType(0, "T3");
//
//        difficultyBox.put(difficultyOne);
//        difficultyBox.put(difficultyTwo);
//        difficultyBox.put(difficultyThree);
//        difficultyBox.put(difficultyFour);
//
//        try {
//            assertEquals(2, difficultTypeDao.find("typename", "T3").size());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void countTest(){
//        DifficultType difficultyOne = new DifficultType(0, "T1");
//        DifficultType difficultyTwo = new DifficultType(0, "T2");
//        DifficultType difficultyThree = new DifficultType(0, "T3");
//        DifficultType difficultyFour = new DifficultType(0, "T3");
//
//        difficultyBox.put(difficultyOne);
//        difficultyBox.put(difficultyTwo);
//        difficultyBox.put(difficultyThree);
//        difficultyBox.put(difficultyFour);
//
//        assertEquals(4, difficultTypeDao.count());
//    }
//
//    @Test
//    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
//        DifficultType difficultyOne = new DifficultType(0, "T1");
//        DifficultType difficultyTwo = new DifficultType(0, "T2");
//        DifficultType difficultyThree = new DifficultType(0, "T3");
//        DifficultType difficultyFour = new DifficultType(0, "T3");
//
//        difficultyBox.put(difficultyOne);
//        difficultyBox.put(difficultyTwo);
//        difficultyBox.put(difficultyThree);
//        difficultyBox.put(difficultyFour);
//
//        assertEquals(2, difficultTypeDao.count("typename", "T3"));
//    }
//
//    @Test
//    public void deleteTest(){
//        DifficultType difficultyOne = new DifficultType(0, "T1");
//        DifficultType difficultyTwo = new DifficultType(0, "T2");
//
//        difficultyBox.put(difficultyOne);
//        difficultyBox.put(difficultyTwo);
//
//        try {
//            difficultTypeDao.delete("typename", "T1");
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//
//        assertEquals(1, difficultTypeDao.find().size());
//    }
//
//    @Test
//    public void deleteAllTest(){
//        DifficultType difficultyOne = new DifficultType(0, "T1");
//        DifficultType difficultyTwo = new DifficultType(0, "T2");
//
//        difficultyBox.put(difficultyOne);
//        difficultyBox.put(difficultyTwo);
//
//        difficultTypeDao.deleteAll();
//        assertEquals(0, difficultTypeDao.count());
//    }
//
//    @After
//    public void after() {
//        difficultyBox.removeAll();
//        boxStore.close();
//    }
}
