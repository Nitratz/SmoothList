package com.list.smoothlist;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.list.smoothlist.activity.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MiniCreateTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void miniCreateTest() {
        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.floating_button),
                        withParent(allOf(withId(R.id.coord),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.title), isDisplayed()));
        appCompatEditText.perform(replaceText("Hey"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.desc), isDisplayed()));
        appCompatEditText2.perform(replaceText("Hello world"), closeSoftKeyboard());

        pressBack();

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.ok), withText("OK"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.title), withText("Hey"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.header),
                                        1),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Hey")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.date_field), withText("No due date"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.header),
                                        1),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("No due date")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.desc), withText("Hello world"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.card),
                                        0),
                                2),
                        isDisplayed()));
        textView3.check(matches(withText("Hello world")));
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
