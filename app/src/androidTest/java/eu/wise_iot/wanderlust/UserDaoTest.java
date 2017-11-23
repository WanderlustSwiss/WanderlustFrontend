package eu.wise_iot.wanderlust;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import eu.wise_iot.wanderlust.model.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.model.DatabaseModel.User;
import eu.wise_iot.wanderlust.model.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.model.DatabaseModel.User_;
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
    public void insertUserTest(){
        userDao.create(testUser);
        assertEquals("TestUser1", userQueryBuilder.equal(User_.nickname, "TestUser1").build().findFirst().getNickname());
    }

    @Test
    public void findOne(){
        userBox.put(testUser);
        try {
            assertEquals("TestUser1", userDao.findOne("nickname", "TestUser1").getNickname());
        }catch (NoSuchFieldException | IllegalAccessException e){

        }
    }

    @After
    public void after() {
        boxStore.close();
    }
}
