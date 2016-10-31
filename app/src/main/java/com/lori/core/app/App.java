package com.lori.core.app;

import android.support.multidex.MultiDexApplication;
import com.lori.core.app.util.ComponentReflectionInjector;
import com.lori.core.app.util.Injector;

/**
 * @author artemik
 */
public class App extends MultiDexApplication implements Injector {

    private ComponentReflectionInjector<AppComponent> injector;

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        injector = new ComponentReflectionInjector<>(AppComponent.class, component);
    }

    @Override
    public void inject(Object target) {
        injector.inject(target);
    }
}