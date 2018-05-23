package eu.wise_iot.wanderlust.views.dialog;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.ImageInfo;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Fabian Schwander
 * @license MIT
 */
@RunWith(AndroidJUnit4.class)
public class PoiViewDialogTest {

    @Rule
    public final ActivityTestRule<MainActivity> testActivity = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity;

    private PoiViewDialog poiViewDialog;
    private View view;
    private List<ImageInfo> list;

    private final Poi poi = new Poi(1, "name of poi", "description of poi",  2,
            3, 1000, 0, 1, 0, true, list, "", "");


    @Before
    public void setUp() {
        mainActivity = testActivity.getActivity();
        poiViewDialog = PoiViewDialog.newInstance(poi);

        FrameLayout frameLayout = mainActivity.findViewById(R.id.content_frame);
        assertNotNull("FrameLayout is null", frameLayout);

        mainActivity.getFragmentManager().beginTransaction()
                .add(frameLayout.getId(), poiViewDialog)
                .commit();

        getInstrumentation().waitForIdleSync();

        view = poiViewDialog.getView();
        assertNotNull("view is null", view);
    }

    @Test
    public void testImageView() {
        ImageView poiImage = view.findViewById(R.id.poi_image);
        assertNotNull(poiImage);
    }

    @Test
    public void testTextViews() {
        TextView typeTextView = view.findViewById(R.id.poi_type_text_view);
        assertNotNull(typeTextView);
            String[] typeValues = mainActivity.getResources().getStringArray(R.array.dialog_feedback_spinner_type);
        assertEquals(typeValues[0], typeTextView.getText().toString());

        //TextView elevationTextView = (TextView) view.findViewById(R.id.poi_elevation_text_view);
        //assertNotNull(elevationTextView);

        TextView titleTextView = view.findViewById(R.id.poi_title_text_view);
        assertNotNull(titleTextView);
        assertEquals("name of poi", titleTextView.getText().toString());

        TextView dateTextView = view.findViewById(R.id.poi_date_text_view);
        assertNotNull(dateTextView);

        TextView descriptionTextView = view.findViewById(R.id.poi_description_text_view);
        assertNotNull(descriptionTextView);
        assertEquals("description of poi", descriptionTextView.getText().toString());
    }

    public void testButtons() {
        ImageButton closeDialogButton = view.findViewById(R.id.poi_close_dialog_button);
        assertNotNull(closeDialogButton);

        ImageButton editPoiButton = view.findViewById(R.id.poi_edit_button);
        assertNotNull(editPoiButton);

        ImageButton deletePoiButton = view.findViewById(R.id.poi_delete_button);
        assertNotNull(deletePoiButton);
    }
}