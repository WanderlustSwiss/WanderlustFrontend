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

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.models.DatabaseModel.Poi;
import eu.wise_iot.wanderlust.views.MainActivity;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * @author Fabian Schwander
 * @license MIT
 */
@RunWith(AndroidJUnit4.class)
public class ViewPoiDialogTest {

    @Rule
    public ActivityTestRule<MainActivity> testActivity = new ActivityTestRule<>(MainActivity.class);

    private MainActivity mainActivity;

    private ViewPoiDialog viewPoiDialog;
    private View view;

    private Poi poi = new Poi(1, "name of poi", "description of poi", null, 2,
            3, 1000, 0, 1, 0, true, 0);


    @Before
    public void setUp() throws Exception {
        mainActivity = testActivity.getActivity();
        viewPoiDialog = ViewPoiDialog.newInstance(poi);

        FrameLayout frameLayout = (FrameLayout) mainActivity.findViewById(R.id.content_frame);
        assertNotNull("FrameLayout is null", frameLayout);

        mainActivity.getFragmentManager().beginTransaction()
                .add(frameLayout.getId(), viewPoiDialog)
                .commit();

        getInstrumentation().waitForIdleSync();

        view = viewPoiDialog.getView();
        assertNotNull("view is null", view);
    }

    @Test
    public void testImageView() {
        ImageView poiImage = (ImageView) view.findViewById(R.id.poi_image);
        assertNotNull(poiImage);
    }

    @Test
    public void testTextViews() {
        TextView typeTextView = (TextView) view.findViewById(R.id.poi_type_text_view);
        assertNotNull(typeTextView);
        String[] typeValues = mainActivity.getResources().getStringArray(R.array.dialog_feedback_spinner_type);
        assertEquals(typeValues[0], typeTextView.getText().toString());

        TextView elevationTextView = (TextView) view.findViewById(R.id.poi_elevation_text_view);
        assertNotNull(elevationTextView);

        TextView titleTextView = (TextView) view.findViewById(R.id.poi_title_text_view);
        assertNotNull(titleTextView);
        assertEquals("name of poi", titleTextView.getText().toString());

        TextView dateTextView = (TextView) view.findViewById(R.id.poi_date_text_view);
        assertNotNull(dateTextView);

        TextView descriptionTextView = (TextView) view.findViewById(R.id.poi_description_text_view);
        assertNotNull(descriptionTextView);
        assertEquals("description of poi", descriptionTextView.getText().toString());
    }

    public void testButtons() {
        ImageButton closeDialogButton = (ImageButton) view.findViewById(R.id.poi_close_dialog_button);
        assertNotNull(closeDialogButton);

        ImageButton editPoiButton = (ImageButton) view.findViewById(R.id.poi_edit_button);
        assertNotNull(editPoiButton);

        ImageButton deletePoiButton = (ImageButton) view.findViewById(R.id.poi_delete_button);
        assertNotNull(deletePoiButton);
    }
}