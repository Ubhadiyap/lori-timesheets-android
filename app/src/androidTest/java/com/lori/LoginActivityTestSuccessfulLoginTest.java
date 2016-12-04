package com.lori;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.lori.base.BaseInstrumentationTest;
import com.lori.ui.activity.LauncherActivity;
import com.lori.ui.presenter.LoginActivityPresenter;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.lori.ui.presenter.LoginActivityPresenter.LOGIN_REQUEST;
import static org.hamcrest.Matchers.not;

/**
 * @author artemik
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTestSuccessfulLoginTest extends BaseInstrumentationTest {

    @Rule
    public ActivityTestRule<LauncherActivity> mActivityRule = new ActivityTestRule<>(LauncherActivity.class);

    @Test
    public void loginButtonClicked_ValidInput_SuccessfulLogin() throws IOException {
        MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse().setBody(UUID.randomUUID().toString())); // Login.
        server.enqueue(new MockResponse().setBody("[{\n" +
                "    \"id\" : \"ts$ExtUser-acd9574f-0919-4e34-b72a-842a076d55fa\",\n" +
                "    \"login\" : \"admin\"\n" +
                "}]")); // Load user.
        server.start();

        onView(withId(R.id.serverUrlInputText)).perform(clearText(), typeText(server.url("").toString()));
        onView(withId(R.id.serverUrlInputText)).perform(closeSoftKeyboard());

        block(LoginActivityPresenter.class, LOGIN_REQUEST);
        onView(withId(R.id.signInButton)).perform(scrollTo()).perform(click());
        onView(withId(R.id.signInButton)).check(matches(not(isClickable())));
        releaseAndWaitFor(LoginActivityPresenter.class, LOGIN_REQUEST);

        server.shutdown();
    }
}