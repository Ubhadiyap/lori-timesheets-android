package com.lori.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import icepick.Icepick;
import nucleus.presenter.RxPresenter;
import org.greenrobot.eventbus.EventBus;
import rx.Observable;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

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

    /**
     * Non-restartable version of restartableFirst().
     */
    protected <T> void first(final Func0<Observable<T>> observableFactory, final Action2<ViewType, T> onNext,
                             @Nullable final Action2<ViewType, Throwable> onError) {
        add(
                observableFactory.call()
                        .compose(deliverFirst())
                        .subscribe(split(onNext, onError))
        );
    }

    /**
     * A restartable version that returns nothing.
     */
    protected void restartable(int restartableId, Function<ViewType> function) {
        restartable(restartableId,
                () -> view().filter(view -> view != null)
                        .take(1)
                        .subscribe(function::call)
        );
    }

    protected <T> void restartableFirstAsync(int restartableId, final Func0<Observable<T>> observableFactory,
                                             final Action2<ViewType, T> onNext, @Nullable final Action2<ViewType, Throwable> onError) {
        restartableFirst(restartableId,
                () -> Observable.defer(observableFactory)
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                ,
                onNext,
                onError);
    }

    public interface Function<View> {
        void call(View view);
    }
}
