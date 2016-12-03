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
public class WeekActivityPresenter extends BasePresenter<WeekActivity> {
    private static final String TAG = WeekActivityPresenter.class.getSimpleName();

    public static final int LOGOUT = 0;

    @Inject
    LoginService loginService;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(LOGOUT,
                () -> loginService.logout(),
                (weekActivity, aVoid) -> {
                    getView().finishAffinity();

                    Intent i = new Intent(getView(), LauncherActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    getView().startActivity(i);
                },
                (weekActivity, throwable) -> {
                    Log.e(TAG, "Couldn't logout", throwable);
                    weekActivity.showNetworkError();
                }
        );
    }

    public void onGoTodayClick() {
        getView().showCurrentWeek();
    }

    public void onLogoutClick() {
        start(LOGOUT);
    }
}
