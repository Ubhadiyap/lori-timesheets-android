package com.lori;

import android.util.Pair;
import com.lori.ui.base.CustomRxPresenter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author artemik
 */
public class BackgroundTasksTestManager implements CustomRxPresenter.TestListener {
    private final Map<Pair<Class, Integer>, CountDownLatch> taskCompletionBlocks = new HashMap<>();
    private final Map<Pair<Class, Integer>, CountDownLatch> resultWaitBlocks = new HashMap<>();

    public void tearDown() {
        taskCompletionBlocks.clear();
        resultWaitBlocks.clear();
    }

    public void block(Class presenterClass, int id) {
        synchronized (BackgroundTasksTestManager.class) {

            taskCompletionBlocks.put(new Pair<>(presenterClass, id), new CountDownLatch(1));

        }
    }

    public void release(Class presenterClass, int id) {
        synchronized (BackgroundTasksTestManager.class) {

            CountDownLatch latch = taskCompletionBlocks.get(new Pair<>(presenterClass, id));
            if (latch != null) {
                latch.countDown();
            }

        }
    }

    public void waitForCompletion(Class presenterClass, int id) {
        CountDownLatch latch;
        synchronized (BackgroundTasksTestManager.class) {

            Pair<Class, Integer> key = new Pair<>(presenterClass, id);
            latch = resultWaitBlocks.get(key);
            //noinspection Java8ReplaceMapGet
            if (latch == null) {
                latch = new CountDownLatch(1);
                resultWaitBlocks.put(key, latch);
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseAndWaitFor(Class presenterClass, int id) {
        release(presenterClass, id);
        waitForCompletion(presenterClass, id);
    }

    @Override
    public void waitIfNeeded(Class presenterClass, int id) {
        CountDownLatch latch;
        synchronized (BackgroundTasksTestManager.class) {
            latch = taskCompletionBlocks.get(new Pair<>(presenterClass, id));
            if (latch == null) {
                return;
            }
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(Class presenterClass, int id) {
        synchronized (BackgroundTasksTestManager.class) {

            Pair<Class, Integer> key = new Pair<>(presenterClass, id);
            CountDownLatch latch = resultWaitBlocks.get(key);
            if (latch == null) {
                resultWaitBlocks.put(key, new CountDownLatch(0));
            } else {
                latch.countDown();
            }
        }
    }
}
