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

    private ViewType cachedView;

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
    protected void onTakeView(ViewType viewType) {
        cachedView = viewType; //TODO: move this logic to Presenter. This is for getting view when it's even been dropped.
        super.onTakeView(viewType);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cachedView = null;

        if (eventBus != null && eventBus.isRegistered(this)) {
            eventBus.unregister(this);
        }
    }

    /**
     * Returns view regardless of its state (taken or dropped).
     * This is needed for operations on view that don't require it being run and shown.
     */
    protected ViewType forceGetView() {
        return cachedView;
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
     * A non-restartable version that returns nothing.
     */
    protected void first(Function<ViewType> function) {
        add(view().filter(view -> view != null)
                .take(1)
                .subscribe(function::call));
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
