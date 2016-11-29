package com.lori.core.app;

import android.support.multidex.MultiDexApplication;
import com.lori.core.app.util.ComponentReflectionInjector;
import com.lori.core.app.util.Injector;

/**
 * @author artemik
 */
public class App extends MultiDexApplication implements Injector {

    private ComponentReflectionInjector<AppComponent> injector;
    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = createAppComponent();
        injector = new ComponentReflectionInjector(component.getClass(), component);
    }

    protected AppComponent createAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public void inject(Object target) {
        injector.inject(target);
    }

    public AppComponent getAppComponent() {
        return component;
    }
}