package eu.wise_iot.wanderlust.views.dialog;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.controllers.ControllerEvent;
import eu.wise_iot.wanderlust.controllers.DatabaseController;
import eu.wise_iot.wanderlust.controllers.EventType;
import eu.wise_iot.wanderlust.controllers.FragmentHandler;
import eu.wise_iot.wanderlust.controllers.PoiController;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.models.DatabaseModel.User;
import eu.wise_iot.wanderlust.views.LoginFragment;
import eu.wise_iot.wanderlust.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * @author Fabian Schwander
 * @license MIT
 */
@RunWith(AndroidJUnit4.class)
public class EditPoiDialogTest {

    @Rule
    public ActivityTestRule<MainActivity> testActivity = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity;

    private PoiController poiController;
    private EditPoiDialog editPoiDialog;
    private View view;

    private Poi poi = new Poi(1, "name", "description", null, 2,
            3, 0, 1, 0, true, 0);

    @Before
    public void setUp() throws Exception {
        mainActivity = testActivity.getActivity();
        editPoiDialog = EditPoiDialog.newInstance(poi);

        poiController = new PoiController();

        FrameLayout frameLayout = (FrameLayout) mainActivity.findViewById(R.id.content_frame);
        assertNotNull("FrameLayout is null", frameLayout);

        mainActivity.getFragmentManager().beginTransaction()
                .add(frameLayout.getId(), editPoiDialog)
                .commit();

        getInstrumentation().waitForIdleSync();

        view = editPoiDialog.getView();
        assertNotNull("view is null", view);

//        User user = new User(1, "nickname", "email", "password", 1, true, true, "lastLogin", "addountType");
    }


    @Test
    public void testSaveButton() {
        ImageButton buttonSave = (ImageButton) view.findViewById(R.id.poi_save_button);
        assertNotNull("button save is null", buttonSave);
    }

    @Test
    public void testDeleteButton() {
        ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.poi_delete_button);

//        if (poiController.isOwnerOf(poi)) {
//            assertNotNull("button delete is null", buttonDelete);
//        } else {
            assertNull("button delete is accessible, even tough the user is not the owner", buttonDelete);
//        }
    }

    @After
    public void tearDown() throws Exception {
        mainActivity = null;
    }
}