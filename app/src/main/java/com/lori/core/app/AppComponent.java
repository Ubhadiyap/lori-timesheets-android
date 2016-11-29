package com.lori.core.app;

import com.lori.core.service.SessionService;
import com.lori.ui.activity.LauncherActivity;
import com.lori.ui.activity.WeekActivity;
import com.lori.ui.presenter.*;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author artemik
 */
@Singleton
@Component(modules = {AppModule.class, TimeEntryServiceModule.class, LoginServiceModule.class})
public interface AppComponent {

    SessionService sessionService();

    void inject(WeekActivity x);

    void inject(LauncherActivity x);

    void inject(WeekFragmentPresenter x);

    void inject(WeekTotalFragmentPresenter x);

    void inject(DayFragmentPresenter x);

    void inject(TimeEntryEditDialogPresenter x);

    void inject(LoginActivityPresenter x);

    void inject(WeekActivityPresenter x);

    void inject(LauncherActivityPresenter x);
}