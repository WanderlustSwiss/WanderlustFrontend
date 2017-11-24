package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseModel.User_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


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
        testUser = new User(0, "TestUser1", "TestUserMailAdress", "TestUserPassword");
    }

    @Test
    public void createTest(){
        userDao.create(testUser);
        assertEquals("TestUser1", userQueryBuilder.equal(User_.nickname, "TestUser1").build().findFirst().getNickname());
    }

    @Test
    public void findOneTest(){
        userBox.put(testUser);
        try {
            assertEquals("TestUser1", userDao.findOne("nickname", "TestUser1").getNickname());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @Test
    public void findTest(){
        User userOne = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
        User userTwo = new User(0, "TestUser3", "TestUser2Mail", "TestUser2Password");

        userBox.put(userOne);
        userBox.put(userTwo);

        assertEquals(2, userDao.find().size());
    }

    @Test
    public void findDetailedTest(){
        User userOne = new User(0, "TestUser1", "TestUser2Mail", "TestUser2Password");
        User userTwo = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");
        User userThree = new User(0, "TestUser3", "TestUser2Mail", "TestUser2Password");
        User userFour = new User(0, "TestUser4", "TestUser3Mail", "TestUser2Password");

        userBox.put(userOne);
        userBox.put(userTwo);
        userBox.put(userThree);
        userBox.put(userFour);

        try {
            assertEquals(3, userDao.find("mail", "TestUser2Mail").size());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    public void removeTest(){
        User userOne = new User(0, "TestUser1", "TestUser2Mail", "TestUser2Password");
        User userTwo = new User(0, "TestUser2", "TestUser2Mail", "TestUser2Password");

        userBox.put(userOne);
        userBox.put(userTwo);

        try {
            userDao.delete("nickname", "TestUser1");
        }catch (NoSuchFieldException | IllegalAccessException e){

        }

        assertEquals(1, userDao.find().size());
    }

    @After
    public void after() {
        userBox.removeAll();
        boxStore.close();
    }
}
