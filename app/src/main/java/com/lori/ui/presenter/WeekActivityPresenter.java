package com.lori.ui.presenter;

import android.content.Intent;
import com.lori.core.service.LoginService;
import com.lori.ui.activity.LauncherActivity;
import com.lori.ui.activity.WeekActivity;
import com.lori.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * @author artemik
 */
public class WeekActivityPresenter extends BasePresenter<WeekActivity> {

    @Inject
    LoginService loginService;

    public void onGoTodayClick() {
        getView().showCurrentWeek();
    }

    public void onLogoutClick() {
        loginService.logout();
        getView().finishAffinity();

        Intent i = new Intent(getView(), LauncherActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getView().startActivity(i);
    }
}
