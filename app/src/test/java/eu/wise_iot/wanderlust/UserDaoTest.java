package eu.wise_iot.wanderlust;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.app.Instrumentation;
import android.content.Context;

import eu.wise_iot.wanderlust.model.MyObjectBox;
import eu.wise_iot.wanderlust.model.User;
import eu.wise_iot.wanderlust.model.UserDao;
import eu.wise_iot.wanderlust.model.User_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;


/**
 * Created by rilindgashi on 22.11.17.
 */

public class UserDaoTest extends Instrumentation{

    BoxStore boxStore;
    Box<User> userBox;
    QueryBuilder<User> userQueryBuilder;
    User testUser;
    UserDao userDao;

    @Before
    public void setUpBefore(){
        //TODO: Context von irgendwo herholen
        //Context context = InstrumentationRegistry.getContext();
        //boxStore = MyObjectBox.builder().androidContext(context).build();
        userDao = new UserDao(boxStore);
        userBox = boxStore.boxFor(User.class);
        userQueryBuilder = userBox.query();
        testUser = new User(0, "TestUser1", "TestUserMailAdress", "TestUserPassword");

    }

    @Test
    public void insertUserTest(){
        userDao.insertUser(testUser);
        assertEquals("TestUser1", userQueryBuilder.equal(User_.nickname, "TestUser1").build().findFirst().getNickname());
    }

    @Test
    public void findOne(){
        userBox.put(testUser);
        assertEquals("TestUser1", userDao.findOne("nickname", "TestUser1").getNickname());
    }



}
