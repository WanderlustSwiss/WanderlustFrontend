package eu.wise_iot.wanderlust.views;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.*;
import static android.support.test.espresso.matcher.ViewMatchers.*;

import eu.wise_iot.wanderlust.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ViewProfileEspressoTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void viewProfileEspressoTest() {
         // Added a sleep statement to match the app's execution delay.
 // The recommended way to handle such scenarios is to use Espresso idling resources:
  // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
try {
 Thread.sleep(60000);
 } catch (InterruptedException e) {
 e.printStackTrace();
 }
        
        ViewInteraction appCompatTextView = onView(
allOf(withId(R.id.link_login), withText("Bereits registriert? Login"),
childAtPosition(
childAtPosition(
withId(R.id.content_frame),
0),
5),
isDisplayed()));
        appCompatTextView.perform(click());
        
        ViewInteraction appCompatEditText = onView(
allOf(withId(R.id.input_nickname_email),
childAtPosition(
childAtPosition(
withId(R.id.text_input_layout_nickname_email),
0),
0),
isDisplayed()));
        appCompatEditText.perform(replaceText("WanderlustAdmin"), closeSoftKeyboard());
        
        ViewInteraction appCompatEditText2 = onView(
allOf(withId(R.id.input_password),
childAtPosition(
childAtPosition(
withId(R.id.text_input_layout_password),
0),
0),
isDisplayed()));
        appCompatEditText2.perform(replaceText("h3FXq-FYg.nf"), closeSoftKeyboard());
        
        ViewInteraction appCompatButton = onView(
allOf(withId(R.id.btn_signin), withText("Login"),
childAtPosition(
childAtPosition(
withId(R.id.content_frame),
1),
2),
isDisplayed()));
        appCompatButton.perform(click());
        
        pressBack();
        
        ViewInteraction appCompatImageButton = onView(
allOf(withContentDescription("Menu öffnen"),
childAtPosition(
allOf(withId(R.id.toolbar),
childAtPosition(
withClassName(is("android.support.design.widget.AppBarLayout")),
0)),
1),
isDisplayed()));
        appCompatImageButton.perform(click());
        
        ViewInteraction navigationMenuItemView = onView(
allOf(childAtPosition(
allOf(withId(R.id.design_navigation_view),
childAtPosition(
withId(R.id.nav_view),
0)),
3),
isDisplayed()));
        navigationMenuItemView.perform(click());
        
        ViewInteraction textView = onView(
allOf(withText("WanderlustAdmin"),
childAtPosition(
allOf(withId(R.id.toolbar),
childAtPosition(
IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
0)),
1),
isDisplayed()));
        textView.check(matches(withText("WanderlustAdmin")));
        
        ViewInteraction button = onView(
allOf(withId(R.id.editProfileButton),
childAtPosition(
childAtPosition(
IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
1),
6),
isDisplayed()));
        button.check(matches(isDisplayed()));
        
        ViewInteraction listView = onView(
allOf(withId(R.id.listContent),
childAtPosition(
allOf(withId(R.id.profileTabContent),
childAtPosition(
withId(R.id.profileLayout),
2)),
0),
isDisplayed()));
        listView.check(matches(isDisplayed()));
        
        }

        private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup)parent).getChildAt(position));
            }
        };
    }
    }
