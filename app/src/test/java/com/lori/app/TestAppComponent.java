package com.lori.app;

import com.lori.LoginActivityPresenterTest;
import com.lori.core.app.AppComponent;
import com.lori.core.app.AppModule;
import com.lori.core.app.LoginServiceModule;
import com.lori.core.app.TimeEntryServiceModule;
import dagger.Component;

import javax.inject.Singleton;

/**
 * @author artemik
 */
@Singleton
@Component(modules = {AppModule.class, TimeEntryServiceModule.class, LoginServiceModule.class})
public interface TestAppComponent extends AppComponent {
    void inject(LoginActivityPresenterTest x);
}
