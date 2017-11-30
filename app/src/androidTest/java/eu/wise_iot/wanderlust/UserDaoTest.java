package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.LoginUser;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
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
 * @author Rilind Gashi
 */

@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

    BoxStore boxStore;
    Box<User> userBox;
    QueryBuilder<User> userQueryBuilder;
    User testUser;
    UserDao userDao;

    @Before
    public void setUpBefore(){
        Context appContext = InstrumentationRegistry.getTargetContext();
        boxStore = MyObjectBox.builder().androidContext(appContext).build();
        userDao = new UserDao(boxStore);
        userBox = boxStore.boxFor(User.class);
        userQueryBuilder = userBox.query();
        testUser = new User(1, "TestUser14", "Test14@UserMailAdress.com", "Password123",
                0,true,true,"12.22.2202","local");

        LoginService loginService = ServiceGenerator.createService(LoginService.class);

        LoginUser testUser = new LoginUser("lauchgesichtzs", "Ha11loW4lt");
        Call<LoginUser> call = loginService.basicLogin(testUser);
        call.enqueue(new Callback<LoginUser>() {
            @Override
            public void onResponse(Call<LoginUser> call, Response<LoginUser> response) {
                if (response.isSuccessful()) {
                    Headers headerResponse = response.headers();
                    //convert header to Map
                    Map<String, List<String>> headerMapList = headerResponse.toMultimap();
                    LoginUser.setCookies((ArrayList<String>) headerMapList.get("Set-Cookie"));

                }else{
                    fail();
                }
            }
            @Override
            public void onFailure(Call<LoginUser> call, Throwable t) {
                fail();
            }
        });
    }

    @Test
    public void createTest(){
        try {
            CountDownLatch doneSignal = new CountDownLatch(1);
            userDao.create(testUser, event -> {
                switch (event.getType()) {
                    case OK:
                        doneSignal.countDown();
                        break;
                    default:
                        fail(event.getType().toString());
                }
            });
            doneSignal.await();
        } catch (Exception e){
            fail();
        }
        assertEquals(userBox.get(1).getUser_id(),testUser.getUser_id());
        assertEquals(userBox.get(1).getAccountType(), testUser.getAccountType());
        assertEquals(userBox.get(1).isActive(),testUser.isActive());
        assertEquals(userBox.get(1).getEmail(), testUser.getEmail());
        assertEquals(userBox.get(1).getPassword(), testUser.getPassword());
        assertEquals(userBox.get(1).getNickname(), testUser.getNickname());
    }
    @Test
    public void updateTest(){
        try {
            CountDownLatch doneSignal = new CountDownLatch(1);
            userDao.update(testUser, event -> {
                switch (event.getType()) {
                    case OK:
                        doneSignal.countDown();
                        break;
                    default:
                        fail(event.getType().toString());
                }
            });
            doneSignal.await();
        } catch (Exception e){
            fail();
        }
        assertEquals(userBox.get(1).getEmail(), testUser.getEmail());
        assertEquals(userBox.get(1).getPassword(), testUser.getPassword());
        assertEquals(userBox.get(1).getNickname(), testUser.getNickname());
    }
//    @Test
//    public void updateLocallyTest(){
//        userBox.put(testUser);
//        testUser.setNickname("UpdatedTestUser");
//        userBox.put(testUser);
//        assertEquals("UpdatedTestUser", userQueryBuilder.equal(User.get, testUser.getUser_id()).build().findFirst().getNickname());
//    }
//
//    @Test
//    public void findOneTest(){
//        userBox.put(testUser);
//        try {
//            assertEquals("TestUser1", userDao.findOne("nickname", "TestUser1").getNickname());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void findTest(){
//        User userOne = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
//        User userTwo = new User(0, "TestUser3", "TestUser2Mail", "TestUser2Password");
//
//        userBox.put(userOne);
//        userBox.put(userTwo);
//
//        assertEquals(2, userDao.find().size());
//    }
//
//    @Test
//    public void findDetailedTest(){
//        User userOne = new User(0, "TestUser1", "TestUser2Mail", "TestUser2Password");
//        User userTwo = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
//        User userThree = new User(0, "TestUser3", "TestUser2Mail", "TestUser2Password");
//        User userFour = new User(0, "TestUser4", "TestUser3Mail", "TestUser2Password");
//
//        userBox.put(userOne);
//        userBox.put(userTwo);
//        userBox.put(userThree);
//        userBox.put(userFour);
//
//        try {
//            assertEquals(3, userDao.find("mail", "TestUser2Mail").size());
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//    }
//
//    @Test
//    public void countTest(){
//        User userOne = new User(0, "TestUser1", "TestUser2Mail", "TestUser2Password");
//        User userTwo = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
//        User userThree = new User(0, "TestUser3", "TestUser2Mail", "TestUser2Password");
//        User userFour = new User(0, "TestUser4", "TestUser3Mail", "TestUser2Password");
//
//        userBox.put(userOne);
//        userBox.put(userTwo);
//        userBox.put(userThree);
//        userBox.put(userFour);
//
//        assertEquals(4, userDao.count());
//    }
//
//    @Test
//    public void countTestTwo() throws NoSuchFieldException, IllegalAccessException {
//        User userOne = new User(0, "TestUser1", "TestUser2Mail", "TestUser2Password");
//        User userTwo = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
//        User userThree = new User(0, "TestUser3", "TestUser2Mail", "TestUser2Password");
//        User userFour = new User(0, "TestUser4", "TestUser3Mail", "TestUser2Password");
//
//        userBox.put(userOne);
//        userBox.put(userTwo);
//        userBox.put(userThree);
//        userBox.put(userFour);
//
//        assertEquals(3, userDao.count("mail", "TestUser2Mail"));
//    }
//
//    @Test
//    public void deleteTest(){
//        User userOne = new User(0, "TestUser1", "TestUser2Mail", "TestUser2Password");
//        User userTwo = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
//
//        userBox.put(userOne);
//        userBox.put(userTwo);
//
//        try {
//            userDao.delete("nickname", "TestUser1");
//        }catch (NoSuchFieldException | IllegalAccessException e){
//
//        }
//
//        assertEquals(1, userDao.find().size());
//    }
//
//    @Test
//    public void deleteAllTest(){
//        User userOne = new User(0, "TestUser1", "TestUser2Mail", "TestUser2Password");
//        User userTwo = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
//
//        userBox.put(userOne);
//        userBox.put(userTwo);
//        userDao.deleteAll();
//        assertEquals(0, userDao.count());
//    }
//
//    @After
//    public void after() {
//        userBox.removeAll();
//        boxStore.close();
//    }
}
