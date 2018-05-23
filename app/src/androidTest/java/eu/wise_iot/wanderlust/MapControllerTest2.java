package eu.wise_iot.wanderlust;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.models.DatabaseModel.MapSearchResult;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.MapFragment;

import static android.support.test.InstrumentationRegistry.getInstrumentation;


/**
 * Fragment which represents the UI of the profile of a user.
 *
 * @author Baris Demirci
 * @license MIT
 */
@RunWith(AndroidJUnit4.class)
public class MapControllerTest2 {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);
    private MapFragment mapFragment;
    private MapController controller;
    private MainActivity activity;

    @Before
    public void setUp() {
        activity = main.getActivity();
        FrameLayout frameLayout = activity.findViewById(R.id.content_frame);
        mapFragment = MapFragment.newInstance();
        activity.getFragmentManager().beginTransaction().add(frameLayout.getId(), mapFragment).commit();
        controller = new MapController(mapFragment);
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void searchExistingPlace() throws Exception {

        controller.searchPlace("Brugg", 1, controllerEvent -> {
            Assert.assertEquals(EventType.OK, controllerEvent.getType());
            ArrayList<MapSearchResult> result = (ArrayList<MapSearchResult>) controllerEvent.getModel();
            Assert.assertEquals(result.size(), 1);
        });
    }

    @Test
    public void searchNotExistingPlace() throws Exception {
        controller.searchPlace("jasiodöfj", 1, controllerEvent -> {
            Assert.assertEquals(EventType.OK, controllerEvent.getType());
            ArrayList<MapSearchResult> result = (ArrayList<MapSearchResult>) controllerEvent.getModel();
            Assert.assertNotNull(result);
            Assert.assertTrue(result.isEmpty());
        });
    }

}