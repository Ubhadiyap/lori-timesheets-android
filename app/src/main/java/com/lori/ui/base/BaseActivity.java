package com.lori.ui.base;

import android.os.Bundle;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lori.core.app.util.Injector;
import icepick.Icepick;
import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;
import nucleus.view.NucleusAppCompatActivity;

/**
 * @author artemik
 */
public abstract class BaseActivity<P extends Presenter> extends NucleusAppCompatActivity<P> {

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final PresenterFactory<P> superFactory = super.getPresenterFactory();
        setPresenterFactory(superFactory == null ? null : (PresenterFactory<P>) () -> {
            P presenter = superFactory.createPresenter();
            ((Injector) getApplication()).inject(presenter);
            return presenter;
        });
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        final int layoutResID = getContentViewId();
        setContentView(layoutResID);

        unbinder = ButterKnife.bind(this, getRoot());

        onContentViewSet();
    }

    protected abstract int getContentViewId();

    protected void onContentViewSet() {
    }

    protected ViewGroup getRoot() {
        return (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        Icepick.saveInstanceState(this, bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
