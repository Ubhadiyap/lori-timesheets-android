package com.lori.core.app;

import android.content.Context;
import android.content.SharedPreferences;
import com.lori.core.service.TimeEntryService;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

/**
 * @author artemik
 */
@Module
public class AppModule {

    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences() {
        return app.getSharedPreferences("settings", 0);
    }

    @Provides
    @Singleton
    EventBus eventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    Context context() {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    TimeEntryService timeEntryService() {
        return new TimeEntryService();
    }
}