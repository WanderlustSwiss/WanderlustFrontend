package eu.wise_iot.wanderlust.ExampleTests;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.wise_iot.wanderlust.R;
import eu.wise_iot.wanderlust.views.MainActivity;
import eu.wise_iot.wanderlust.views.ProfileFragment;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by Baris Demirci on 16.01.2018.
 */
@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> testActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity main;

    @Before
    public void setUp(){
        main = testActivity.getActivity();
    }

    @Test
    public void testScore(){
        FrameLayout frameLayout = (FrameLayout) main.findViewById(R.id.content_frame);
        assertNotNull(frameLayout);

        ProfileFragment profileFragment = new ProfileFragment();
        main.getFragmentManager().beginTransaction()
                                 .add(frameLayout.getId() , profileFragment)
                                 .commit();

        getInstrumentation().waitForIdleSync();

        View view = profileFragment.getView().findViewById(R.id.profileLayout);
        assertNotNull(view);

        TextView textView = (TextView) view.findViewById(R.id.profileAmountScore);
        assertNotNull(textView);

        String score = textView.getText().toString();
        assertEquals("0", score);
    }

    @After
    public void tearDown(){
        main = null;
    }
}