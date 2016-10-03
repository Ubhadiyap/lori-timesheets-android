package com.lori.core.app;

import com.lori.ui.activity.WeekActivity;
import com.lori.ui.presenter.DayFragmentPresenter;
import com.lori.ui.presenter.TimeEntryEditDialogPresenter;
import com.lori.ui.presenter.WeekFragmentPresenter;
import com.lori.ui.presenter.WeekTotalFragmentPresenter;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author artemik
 */
@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(WeekActivity x);
    void inject(WeekFragmentPresenter x);
    void inject(WeekTotalFragmentPresenter x);
    void inject(DayFragmentPresenter x);
    void inject(TimeEntryEditDialogPresenter x);
}