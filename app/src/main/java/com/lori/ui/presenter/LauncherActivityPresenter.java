package com.lori.ui.presenter;

import android.content.Intent;
import android.os.Bundle;
import com.lori.core.service.LoginService;
import com.lori.ui.activity.LauncherActivity;
import com.lori.ui.activity.WeekActivity;
import com.lori.ui.base.BasePresenter;

import javax.inject.Inject;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author artemik
 */
public class LauncherActivityPresenter extends BasePresenter<LauncherActivity> {

    @Inject
    LoginService loginService;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        first(launcherActivity -> {
            if (loginService.isLoggedIn()) {
                launchWeekActivity();
            } else {
                loginService.startLoginActivityIfNotStarted();
                add(loginService.getLoginSubject()
                        .subscribeOn(mainThread())
                        .subscribe(sessionId -> {
                            if (loginService.isLoggedIn()) {
                                launchWeekActivity();
                            } else {
                                forceGetView().finish();
                            }
                        })
                );
            }
        });
    }

    private void launchWeekActivity() {
        Intent intent = new Intent(forceGetView(), WeekActivity.class);
        forceGetView().finish();
        forceGetView().startActivity(intent);
    }
}
