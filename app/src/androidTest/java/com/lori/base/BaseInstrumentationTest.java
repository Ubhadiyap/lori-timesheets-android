package com.lori.base;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.view.View;
import android.widget.EditText;
import com.lori.BackgroundTasksTestManager;
import com.lori.core.app.App;
import com.lori.ui.base.CustomRxPresenter;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.BeforeClass;

import java.util.Collection;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

/**
 * @author artemik
 */
public class BaseInstrumentationTest {

    private static final BackgroundTasksTestManager BACKGROUND_TASKS_TEST_MANAGER = new BackgroundTasksTestManager();

    @BeforeClass
    public static void setup() {
        CustomRxPresenter.backgroundTasksTestListener = BACKGROUND_TASKS_TEST_MANAGER;
    }

    @After
    public void tearDown() {
        BACKGROUND_TASKS_TEST_MANAGER.tearDown();
        clearAppData();
    }

    protected void block(Class presenterClass, int id) {
        BACKGROUND_TASKS_TEST_MANAGER.block(presenterClass, id);
    }

    protected void release(Class presenterClass, int id) {
        BACKGROUND_TASKS_TEST_MANAGER.release(presenterClass, id);
    }

    protected void waitForCompletion(Class presenterClass, int id) {
        BACKGROUND_TASKS_TEST_MANAGER.waitForCompletion(presenterClass, id);
    }

    protected void releaseAndWaitFor(Class presenterClass, int id) {
        BACKGROUND_TASKS_TEST_MANAGER.releaseAndWaitFor(presenterClass, id);
    }

    protected App getApplication() {
        return (TestInstrumentationApplication) InstrumentationRegistry.getTargetContext().getApplicationContext();
    }

    protected void clearAppData() {
        getApplication().getAppComponent().sessionService().clearSession();
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static Matcher<View> withHint(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getHint();

                if (error == null) {
                    return false;
                }

                String hint = error.toString();

                return expectedErrorText.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Expected hint: ");
                description.appendText(expectedErrorText);
            }
        };
    }

    public static <T> Matcher<T> withError(final String expected) {
        return new TypeSafeMatcher<T>() {
            @Override
            protected boolean matchesSafely(T item) {
                if (item instanceof EditText) {
                    return ((EditText) item).getError() != null && ((EditText) item).getError().toString().equals(expected);
                }
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Not found error message [" + expected + "]");
            }
        };
    }

    protected void finishCurrentActivity() {
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection<Activity> resumedActivity = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                for (Activity act : resumedActivity) {
                    act.finishAffinity();
                    break;
                }
            }
        });
    }
}

