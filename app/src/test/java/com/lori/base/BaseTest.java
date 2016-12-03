package com.lori.base;

import android.os.Build;
import com.lori.BackgroundTasksTestManager;
import com.lori.BuildConfig;
import com.lori.app.RxJavaTestRunner;
import com.lori.app.TestApplication;
import com.lori.ui.base.CustomRxPresenter;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author artemik
 */
@RunWith(RxJavaTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP, application = TestApplication.class)
public abstract class BaseTest {

    private static final BackgroundTasksTestManager BACKGROUND_TASKS_TEST_MANAGER = new BackgroundTasksTestManager();

    private ExecutorService mainThreadExecutor = Executors.newSingleThreadExecutor();
    private CustomRxPresenter presenterToTest;

    @Before
    public void setup() {
        CustomRxPresenter.backgroundTasksTestListener = BACKGROUND_TASKS_TEST_MANAGER;

        mainThreadExecutor = Executors.newSingleThreadExecutor();

        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            public Scheduler getMainThreadScheduler() {
                return Schedulers.from(mainThreadExecutor);
            }
        });
    }

    @After
    public void tearDown() {
        presenterToTest.dropView();
        presenterToTest.destroy();
        presenterToTest = null;

        BACKGROUND_TASKS_TEST_MANAGER.tearDown();

        RxAndroidPlugins.getInstance().reset();
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

    protected void onMainThreadBlocking(Runnable runnable) {
        CountDownLatch latch = new CountDownLatch(1);
        Wrapper<Throwable> thrownThrowableWrapper = new Wrapper<>();

        mainThreadExecutor.submit(() -> {
            try {
                runnable.run();
            } catch (Throwable t) {
                thrownThrowableWrapper.wrappedObject = t;
            }
            latch.countDown();
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (thrownThrowableWrapper.wrappedObject != null) {
            throw new RuntimeException(thrownThrowableWrapper.wrappedObject);
        }
    }

    protected <P extends CustomRxPresenter<V>, V> P createPresenter(Class<P> presenterClass, V view) {
        P presenter = createInstance(presenterClass);

        onMainThreadBlocking(() -> {
            getApplication().inject(presenter);
            presenter.create(null);
            presenter.takeView(view);
        });

        presenterToTest = presenter;
        return presenter;
    }

    protected TestApplication getApplication() {
        return (TestApplication) RuntimeEnvironment.application;
    }

    private static <P> P createInstance(Class<P> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static class Wrapper<T> {
        Throwable wrappedObject;
    }
}
