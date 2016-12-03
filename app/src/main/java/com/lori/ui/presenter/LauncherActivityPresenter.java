package com.lori.ui.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.lori.core.service.LoginService;
import com.lori.ui.activity.LauncherActivity;
import com.lori.ui.activity.WeekActivity;
import com.lori.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * @author artemik
 */
public class LauncherActivityPresenter extends BasePresenter<LauncherActivity> {
    private static final String TAG = LauncherActivityPresenter.class.getSimpleName();

    private static final int LISTEN_FOR_LOGIN_RESULT = 0;

    @Inject
    LoginService loginService;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(LISTEN_FOR_LOGIN_RESULT,
                () -> loginService.getLoginSubject(),
                (launcherActivity, sessionId) -> {
                    if (loginService.isLoggedIn()) {
                        launchWeekActivity(launcherActivity);
                    } else {
                        launcherActivity.finish();
                    }
                },
                (launcherActivity, throwable) -> Log.e(TAG, "Error on login result")
        );

        onViewOnce(launcherActivity -> {
            if (loginService.isLoggedIn()) {
                launchWeekActivity(launcherActivity);
            } else {
                loginService.startLoginActivityIfNotStarted();
                start(LISTEN_FOR_LOGIN_RESULT);
            }
        });
    }

    private void launchWeekActivity(LauncherActivity activity) {
        Intent intent = new Intent(activity, WeekActivity.class);
        activity.finish();
        activity.startActivity(intent);
    }
}
