//package eu.wise_iot.wanderlust.views.dialog;
//
//import android.support.design.widget.TextInputLayout;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.Spinner;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import eu.wise_iot.wanderlust.R;
//import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
//import eu.wise_iot.wanderlust.views.MainActivity;
//
//import static android.support.test.InstrumentationRegistry.getInstrumentation;
//import static junit.framework.Assert.assertNotNull;
//import static junit.framework.Assert.assertNull;
//
///**
// * @author Fabian Schwander
// * @license MIT
// */
//@RunWith(AndroidJUnit4.class)
//public class PoiEditDialogTest {
//
//    @Rule
//    public ActivityTestRule<MainActivity> testActivity = new ActivityTestRule<>(MainActivity.class);
//
//    private MainActivity mainActivity;
//
//    private PoiEditDialog poiEditDialog;
//    private View view;
//
//    private Poi poi = new Poi(1, "name", "description", null, 2,
//            3, 1000, 0, 1, 0, true, 0);
//
//    @Before
//    public void setUp() throws Exception {
//        mainActivity = testActivity.getActivity();
//        poiEditDialog = PoiEditDialog.newInstance(poi);
//
//        FrameLayout frameLayout = (FrameLayout) mainActivity.findViewById(R.id.content_frame);
//        assertNotNull("FrameLayout is null", frameLayout);
//
//        mainActivity.getFragmentManager().beginTransaction()
//                .add(frameLayout.getId(), poiEditDialog)
//                .commit();
//
//        getInstrumentation().waitForIdleSync();
//
//        view = poiEditDialog.getView();
//        assertNotNull("view is null", view);
//
////        User user = new User(1, "nickname", "email", "password", 1, true, true, "lastLogin", "addountType");
//    }
//
//
//    @Test
//    public void testSaveButton() {
//        ImageButton buttonSave = (ImageButton) view.findViewById(R.id.poi_save_button);
//        assertNotNull("button_white save is null", buttonSave);
//    }
//
//    @Test
//    public void testDeleteButton() {
//        ImageButton buttonDelete = (ImageButton) view.findViewById(R.id.poi_delete_button);
//
//        // TODO: add check when user is owner of poi
////        if (poiController.isOwnerOf(poi)) {
////            assertNotNull("button_white delete is null", buttonDelete);
////        } else {
//            assertNull("button_white delete is accessible, even tough the user is not the owner", buttonDelete);
////        }
//    }
//
//    @Test
//    public void testInputFields() {
//        EditText titleEditText = (EditText) view.findViewById(R.id.poi_title);
//        assertNotNull(titleEditText);
////        assertEquals(mainActivity.getResources().getString(R.string.poi_fragment_textview_title), titleEditText.getHint());
//
//        TextInputLayout titleTextLayout = (TextInputLayout) view.findViewById(R.id.poi_title_layout);
//        assertNotNull(titleTextLayout);
//
//        EditText descriptionEditText = (EditText) view.findViewById(R.id.poi_description);
//        assertNotNull(descriptionEditText);
////        assertEquals(mainActivity.getResources().getString(R.string.poi_fragment_textview_description), descriptionEditText.getHint());
//
//        Spinner typeSpinner = (Spinner) view.findViewById(R.id.poi_type_spinner);
//        assertNotNull(typeSpinner);
//
//        Spinner modeSpinner = (Spinner) view.findViewById(R.id.poi_mode_spinner);
//        assertNotNull(modeSpinner);
//
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        mainActivity = null;
//    }
//}