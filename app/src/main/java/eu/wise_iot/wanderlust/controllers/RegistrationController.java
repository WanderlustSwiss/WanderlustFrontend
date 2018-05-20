package eu.wise_iot.wanderlust.controllers;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import eu.wise_iot.wanderlust.BuildConfig;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;

/**
 * Registration Controller which handles registrations of the user
 *
 * @author Joshua Meier, Alexander Weinbeck
 * @license MIT
 */
public class RegistrationController {

    private final UserDao userDao;

    /**
     * Create a registration contoller
     */
    public RegistrationController() {
        userDao = UserDao.getInstance();
    }

    /**
     * @param user to register
     * @param handler Creates a user Dao and starts the saving process of an user
     */
    public void registerUser(User user, FragmentHandler handler) {
        userDao.create(user, handler);
    }

    /**
     * perform registration sequential because of user input
     * @param user which should be registered
     * @return event with given response
     */
    public ControllerEvent registerUserSequential(User user){
        final AtomicReference<ControllerEvent> event = new AtomicReference<>();
        try {
            CountDownLatch countDownLatchThread = new CountDownLatch(1);
            registerUser(user, controllerEvent -> {
                event.set(controllerEvent);
                countDownLatchThread.countDown();
            });
            countDownLatchThread.await();
            return event.get();
        } catch (Exception e){
            if (BuildConfig.DEBUG) Log.d("RegistrationController","failure while processing request");
        }
        return event.get();
    }

}
