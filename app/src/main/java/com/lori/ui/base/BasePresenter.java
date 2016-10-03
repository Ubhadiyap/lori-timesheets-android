package com.lori.ui.base;

import android.os.Bundle;
import icepick.Icepick;
import nucleus.presenter.RxPresenter;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * @author artemik
 */
public class BasePresenter<ViewType> extends RxPresenter<ViewType> {

    @Inject
    public EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Icepick.restoreInstanceState(this, savedState);
    }

    @Override
    protected void onSave(Bundle state) {
        super.onSave(state);
        Icepick.saveInstanceState(this, state);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }
}
