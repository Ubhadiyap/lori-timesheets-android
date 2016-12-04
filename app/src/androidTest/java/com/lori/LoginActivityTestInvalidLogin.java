package com.lori;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.lori.base.BaseInstrumentationTest;
import com.lori.ui.activity.LauncherActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * @author artemik
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTestInvalidLogin extends BaseInstrumentationTest {

    @Rule
    public ActivityTestRule<LauncherActivity> mActivityRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void loginButtonClicked_InvalidLogin_ShowInvalidLoginError() {
        String loginPrompt = mActivityRule.getActivity().getString(R.string.prompt_login);
        onView(withId(R.id.loginInputTextLayout)).check(matches(withHint(loginPrompt)));

        onView(withId(R.id.loginInputText)).perform(clearText());
        onView(withId(R.id.loginInputText)).perform(closeSoftKeyboard());

        onView(withId(R.id.signInButton)).perform(scrollTo()).perform(click());

        String invalidLogin = mActivityRule.getActivity().getString(R.string.error_invalid_login);
        onView(withId(R.id.loginInputText)).check(matches(withError(invalidLogin)));
    }
}