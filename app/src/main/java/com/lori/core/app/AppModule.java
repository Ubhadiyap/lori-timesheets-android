package com.lori.core.app;

import android.content.Context;
import android.content.SharedPreferences;
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
    public App app() {
        return app;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences() {
        return app.getSharedPreferences("settings", 0);
    }

    @Provides
    @Singleton
    public EventBus eventBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    public Context context() {
        return app.getApplicationContext();
    }
}