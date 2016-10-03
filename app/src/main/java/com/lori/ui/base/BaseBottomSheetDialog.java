package com.lori.ui.base;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.lori.core.app.util.Injector;
import icepick.Icepick;
import nucleus.factory.PresenterFactory;
import nucleus.presenter.Presenter;

/**
 * @author artemik
 */
public abstract class BaseBottomSheetDialog<P extends Presenter> extends NucleusBottomSheetDialog<P> {

    private Unbinder unbinder;

    public BaseBottomSheetDialog(Application application, Context context) {
        super(context);

        final PresenterFactory<P> superFactory = super.getPresenterFactory();
        setPresenterFactory(superFactory == null ? null : (PresenterFactory<P>) () -> {
            P presenter = superFactory.createPresenter();
            ((Injector) application).inject(presenter);
            return presenter;
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle bundle = super.onSaveInstanceState();
        Icepick.saveInstanceState(this, bundle);
        return bundle;
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onStop() {
        super.onStop();
        unbinder.unbind();
    }
}
