package eu.wise_iot.wanderlust.views;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import eu.wise_iot.wanderlust.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RegistrationFragmentEspressotest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void registrationFragmentEspressotest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.link_registration), withText("Create new account"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        4),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.input_nickname),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_nickname),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("muster"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.input_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("a1111111"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.input_password_repeat),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("a1111111"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.input_password_repeat), withText("a1111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(pressImeActionButton());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_signup), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        1),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.textinput_error), withText("An email is required"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_mail),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3481394);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.input_mail),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_mail),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("muster@muster.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.input_password), withText("a1111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("a111"));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.input_password), withText("a111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.input_password), withText("a111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText8.perform(click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.input_password), withText("a111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText9.perform(replaceText("aaaaaaaa"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.input_password), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText10.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.input_password_repeat), withText("a1111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText11.perform(replaceText("aaaaaaaa"));

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.input_password_repeat), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText12.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.input_password_repeat), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText13.perform(pressImeActionButton());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_signup), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        1),
                                4),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textinput_error), withText("Minimum eight characters, at least one letter and one number"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        1),
                                0),
                        isDisplayed()));
        textView2.check(matches(isDisplayed()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.input_password), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText14.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.input_password), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText15.perform(click());

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.input_password), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText16.perform(click());

        ViewInteraction appCompatEditText17 = onView(
                allOf(withId(R.id.input_password), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText17.perform(replaceText("11111111"));

        ViewInteraction appCompatEditText18 = onView(
                allOf(withId(R.id.input_password), withText("11111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText18.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText19 = onView(
                allOf(withId(R.id.input_password_repeat), withText("aaaaaaaa"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText19.perform(replaceText("11111111"));

        ViewInteraction appCompatEditText20 = onView(
                allOf(withId(R.id.input_password_repeat), withText("11111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText20.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText21 = onView(
                allOf(withId(R.id.input_password_repeat), withText("11111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText21.perform(pressImeActionButton());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btn_signup), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        1),
                                4),
                        isDisplayed()));
        appCompatButton3.perform(click());

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.textinput_error), withText("Minimum eight characters, at least one letter and one number"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        1),
                                0),
                        isDisplayed()));
        textView3.check(matches(isDisplayed()));

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3290664);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText22 = onView(
                allOf(withId(R.id.input_password), withText("11111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText22.perform(replaceText(""));

        ViewInteraction appCompatEditText23 = onView(
                allOf(withId(R.id.input_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText23.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText24 = onView(
                allOf(withId(R.id.input_password_repeat), withText("11111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText24.perform(replaceText(""));

        ViewInteraction appCompatEditText25 = onView(
                allOf(withId(R.id.input_password_repeat),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText25.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText26 = onView(
                allOf(withId(R.id.input_password),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText26.perform(replaceText("aaaa1111"), closeSoftKeyboard());

        ViewInteraction appCompatEditText27 = onView(
                allOf(withId(R.id.input_password_repeat),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText27.perform(replaceText("a1111111"), closeSoftKeyboard());

        ViewInteraction appCompatEditText28 = onView(
                allOf(withId(R.id.input_password_repeat), withText("a1111111"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText28.perform(pressImeActionButton());

        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.btn_signup), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content_frame),
                                        1),
                                4),
                        isDisplayed()));
        appCompatButton4.perform(click());

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.textinput_error), withText("Password don't match"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.text_input_layout_password_repeat),
                                        1),
                                0),
                        isDisplayed()));
        textView4.check(matches(isDisplayed()));

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
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
