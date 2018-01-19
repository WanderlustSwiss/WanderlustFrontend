package eu.wise_iot.wanderlust;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.controllers.ProfileController;
import eu.wise_iot.wanderlust.models.DatabaseModel.MapSearchResult;
import eu.wise_iot.wanderlust.models.DatabaseModel.MyObjectBox;
import eu.wise_iot.wanderlust.models.DatabaseModel.Profile;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.models.DatabaseObject.PoiDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.ProfileDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserDao;
import eu.wise_iot.wanderlust.models.DatabaseObject.UserTourDao;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.MapFragment;
import eu.wise_iot.wanderlust.views.ProfileFragment;
import io.objectbox.BoxStore;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.*;


/**
 * Fragment which represents the UI of the profile of a user.
 *
 * @author Baris Demirci
 * @license MIT
 */
@RunWith(AndroidJUnit4.class)
public class MapControllerTest2 {

    @Rule
    public ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    private MapFragment mapFragment;
    private MapController controller;
    private MainActivity activity;

    @Before
    public void setUp() {
        activity = main.getActivity();
        FrameLayout frameLayout = (FrameLayout) activity.findViewById(R.id.content_frame);
        mapFragment = MapFragment.newInstance();
        activity.getFragmentManager().beginTransaction().add(frameLayout.getId(), mapFragment).commit();
        controller = new MapController(mapFragment);
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void searchExistingPlace() throws Exception {

        controller.searchPlace("Brugg", 1, new FragmentHandler<ArrayList<MapSearchResult>>() {
            @Override
            public void onResponse(ControllerEvent<ArrayList<MapSearchResult>> controllerEvent) {
                Assert.assertEquals(EventType.OK, controllerEvent.getType());
                ArrayList<MapSearchResult> result = controllerEvent.getModel();
                Assert.assertEquals(result.size(), 1);
                Assert.assertNotNull(result.get(0).getLatitude());
                Assert.assertNotNull(result.get(0).getLongitude());
            }
        });
    }

    @Test
    public void searchNotExistingPlace() throws Exception {
        controller.searchPlace("jasiodöfj", 1, new FragmentHandler<ArrayList<MapSearchResult>>() {
            @Override
            public void onResponse(ControllerEvent<ArrayList<MapSearchResult>> controllerEvent) {
                Assert.assertEquals(EventType.OK, controllerEvent.getType());
                ArrayList<MapSearchResult> result = controllerEvent.getModel();
                Assert.assertNotNull(result);
                Assert.assertTrue(result.isEmpty());
            }
        });
    }

}