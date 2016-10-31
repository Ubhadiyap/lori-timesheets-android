package com.lori.core.app;

import com.lori.ui.activity.LauncherActivity;
import com.lori.ui.activity.WeekActivity;
import com.lori.ui.presenter.*;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author artemik
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(WeekActivity x);

    void inject(LauncherActivity x);

    void inject(WeekFragmentPresenter x);

    void inject(WeekTotalFragmentPresenter x);

    void inject(DayFragmentPresenter x);

    void inject(TimeEntryEditDialogPresenter x);

    void inject(LoginActivityPresenter x);
}