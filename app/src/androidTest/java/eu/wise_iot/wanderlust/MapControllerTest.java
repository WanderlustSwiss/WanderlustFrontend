package eu.wise_iot.wanderlust;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.MapController;
import eu.wise_iot.wanderlust.models.DatabaseModel.MapSearchResult;
import eu.wise_iot.wanderlust.views.MapFragment;


@RunWith(AndroidJUnit4.class)
public class MapControllerTest {

    private MapController controller;

    @Before
    public void setUp() {
        controller = new MapController(MapFragment.newInstance());
        MapFragment.newInstance().getActivity();
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

    private static class FragmentUtilActivity extends Activity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            LinearLayout view = new LinearLayout(this);
            view.setId(1);
            setContentView(view);
        }
    }
}
