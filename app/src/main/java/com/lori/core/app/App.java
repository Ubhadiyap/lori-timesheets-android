package com.lori.core.app;

import android.app.Application;
import com.lori.core.app.util.ComponentReflectionInjector;
import com.lori.core.app.util.Injector;
import com.lori.core.gate.lori.retrofit.MockLoriServer;

/**
 * @author artemik
 */
public class App extends Application implements Injector {

    private ComponentReflectionInjector<AppComponent> injector;

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        injector = new ComponentReflectionInjector<>(AppComponent.class, component);

        MockLoriServer.setup();
    }

    @Override
    public void inject(Object target) {
        injector.inject(target);
    }
}