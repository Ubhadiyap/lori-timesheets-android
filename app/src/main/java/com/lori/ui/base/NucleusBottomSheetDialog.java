package com.lori.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import com.rey.material.app.BottomSheetDialog;
import nucleus.factory.PresenterFactory;
import nucleus.factory.ReflectionPresenterFactory;
import nucleus.presenter.Presenter;
import nucleus.view.PresenterLifecycleDelegate;
import nucleus.view.ViewWithPresenter;

/**
 * @author artemik
 */
public abstract class NucleusBottomSheetDialog<P extends Presenter> extends BottomSheetDialog implements ViewWithPresenter<P> {

    private static final String PRESENTER_STATE_KEY = "presenter_state";
    private PresenterLifecycleDelegate<P> presenterDelegate =
            new PresenterLifecycleDelegate<>(ReflectionPresenterFactory.<P>fromViewClass(getClass()));

    public PresenterFactory<P> getPresenterFactory() {
        return presenterDelegate.getPresenterFactory();
    }

    @Override
    public void setPresenterFactory(PresenterFactory<P> presenterFactory) {
        presenterDelegate.setPresenterFactory(presenterFactory);
    }

    public NucleusBottomSheetDialog(Context context) {
        super(context);
    }

    protected void afterConstruct() {
        View root = createView();
        setContentView(root);
        onViewCreated(root);
    }

    public abstract View createView();

    public void onViewCreated(View view) {
    }

    public P getPresenter() {
        return presenterDelegate.getPresenter();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null)
            presenterDelegate.onRestoreInstanceState(bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
        bundle.putBundle(PRESENTER_STATE_KEY, presenterDelegate.onSaveInstanceState());
        return bundle;
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenterDelegate.onResume(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenterDelegate.onDropView();
        presenterDelegate.onDestroy(true);
    }
}
