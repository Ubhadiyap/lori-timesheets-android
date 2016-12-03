package com.lori.ui.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import nucleus.presenter.Presenter;
import nucleus.presenter.delivery.DeliverFirst;
import nucleus.presenter.delivery.DeliverLatestCache;
import nucleus.presenter.delivery.DeliverReplay;
import nucleus.presenter.delivery.Delivery;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author artemik
 */
public class CustomRxPresenter<View> extends Presenter<View> {

    private static final String REQUESTED_KEY = CustomRxPresenter.class.getName() + "#requested";

    private final BehaviorSubject<View> views = BehaviorSubject.create();
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final HashMap<Integer, Func0<Subscription>> restartables = new HashMap<>();
    private final HashMap<Integer, Subscription> restartableSubscriptions = new HashMap<>();
    private final ArrayList<Integer> requested = new ArrayList<>();

    @VisibleForTesting
    public static BackgroundTasksTestListener backgroundTasksTestListener;

    /**
     * Returns an {@link rx.Observable} that emits the current attached view or null.
     * See {@link BehaviorSubject} for more information.
     *
     * @return an observable that emits the current attached view or null.
     */
    public Observable<View> view() {
        return views;
    }

    /**
     * Registers a subscription to automatically unsubscribe it during onDestroy.
     * See {@link CompositeSubscription#add(Subscription) for details.}
     *
     * @param subscription a subscription to add.
     */
    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    /**
     * Removes and unsubscribes a subscription that has been registered with {@link #add} previously.
     * See {@link CompositeSubscription#remove(Subscription)} for details.
     *
     * @param subscription a subscription to remove.
     */
    public void remove(Subscription subscription) {
        subscriptions.remove(subscription);
    }

    /**
     * A restartable is any RxJava observable that can be started (subscribed) and
     * should be automatically restarted (re-subscribed) after a process restart if
     * it was still subscribed at the moment of saving presenter's state.
     * <p>
     * Registers a factory. Re-subscribes the restartable after the process restart.
     *
     * @param restartableId id of the restartable
     * @param factory       factory of the restartable
     */
    public void restartable(int restartableId, Func0<Subscription> factory) {
        restartables.put(restartableId, factory);
        if (requested.contains(restartableId))
            start(restartableId);
    }

    /**
     * Starts the given restartable.
     *
     * @param restartableId id of the restartable
     */
    public void start(int restartableId) {
        stop(restartableId);
        requested.add(restartableId);
        restartableSubscriptions.put(restartableId, restartables.get(restartableId).call());
    }

    /**
     * Unsubscribes a restartable
     *
     * @param restartableId id of a restartable.
     */
    public void stop(int restartableId) {
        requested.remove((Integer) restartableId);
        Subscription subscription = restartableSubscriptions.get(restartableId);
        if (subscription != null)
            subscription.unsubscribe();
    }

    /**
     * Checks if a restartable is unsubscribed.
     *
     * @param restartableId id of the restartable.
     * @return true if the subscription is null or unsubscribed, false otherwise.
     */
    public boolean isUnsubscribed(int restartableId) {
        Subscription subscription = restartableSubscriptions.get(restartableId);
        return subscription == null || subscription.isUnsubscribed();
    }

    public <T> void restartableFirst(int restartableId, final Func0<Observable<T>> observableFactory,
                                     final Action2<View, T> onNext, @Nullable final Action2<View, Throwable> onError) {

        restartable(restartableId, () -> createObservable(restartableId, observableFactory)
                .compose(CustomRxPresenter.this.deliverFirst())
                .subscribe(split(restartableId, onNext, onError)));
    }

    public <T> void restartableLatestCache(int restartableId, final Func0<Observable<T>> observableFactory,
                                           final Action2<View, T> onNext, @Nullable final Action2<View, Throwable> onError) {

        restartable(restartableId, () -> createObservable(restartableId, observableFactory)
                .compose(CustomRxPresenter.this.deliverLatestCache())
                .subscribe(split(restartableId, onNext, onError)));
    }

    public <T> void restartableReplay(int restartableId, final Func0<Observable<T>> observableFactory,
                                      final Action2<View, T> onNext, @Nullable final Action2<View, Throwable> onError) {

        restartable(restartableId, () -> createObservable(restartableId, observableFactory)
                .compose(CustomRxPresenter.this.deliverReplay())
                .subscribe(split(restartableId, onNext, onError)));
    }

    private <T> Observable<T> createObservable(int restartableId, Func0<Observable<T>> observableFactory) {
        return Observable.defer(() -> {
            if (backgroundTasksTestListener != null) {
                backgroundTasksTestListener.waitIfNeeded(CustomRxPresenter.this.getClass(), restartableId);
            }
            return observableFactory.call();
        }).subscribeOn(Schedulers.io()) //+ Constructing observable on background thread is needed for:
                // 1) the construction itself doesn't block the main thread (may be heavy sometimes);
                // 2) in tests, if the background task start is postponed, awaiting happens on the background thread,
                // instead of the main thread, which may be not critical in unit test (the main thread can be replaced),
                // but critical in functional tests where Espresso blocks while the main thread is busy, and so the tests.
                .observeOn(mainThread());
    }

    protected void onViewOnce(Function<View> function) {
        add(view().filter(view -> view != null)
                .take(1)
                .subscribe(function::call));
    }

    protected void restartableOnViewOnce(int restartableId, Function<View> function) {
        restartable(restartableId,
                () -> view().filter(view -> view != null)
                        .take(1)
                        .subscribe(function::call)
        );
    }

    /**
     * Returns an {@link rx.Observable.Transformer} that couples views with data that has been emitted by
     * the source {@link rx.Observable}.
     * <p>
     * {@link #deliverLatestCache} keeps the latest onNext value and emits it each time a new view gets attached.
     * If a new onNext value appears while a view is attached, it will be delivered immediately.
     *
     * @param <T> the type of source observable emissions
     */
    public <T> DeliverLatestCache<View, T> deliverLatestCache() {
        return new DeliverLatestCache<>(views);
    }

    /**
     * Returns an {@link rx.Observable.Transformer} that couples views with data that has been emitted by
     * the source {@link rx.Observable}.
     * <p>
     * {@link #deliverFirst} delivers only the first onNext value that has been emitted by the source observable.
     *
     * @param <T> the type of source observable emissions
     */
    public <T> DeliverFirst<View, T> deliverFirst() {
        return new DeliverFirst<>(views);
    }

    /**
     * Returns an {@link rx.Observable.Transformer} that couples views with data that has been emitted by
     * the source {@link rx.Observable}.
     * <p>
     * {@link #deliverReplay} keeps all onNext values and emits them each time a new view gets attached.
     * If a new onNext value appears while a view is attached, it will be delivered immediately.
     *
     * @param <T> the type of source observable emissions
     */
    public <T> DeliverReplay<View, T> deliverReplay() {
        return new DeliverReplay<>(views);
    }

    public <T> Action1<Delivery<View, T>> split(final int restartableId, final Action2<View, T> onNext, @Nullable final Action2<View, Throwable> onError) {
        return delivery -> {
            try {
                delivery.split(onNext, onError);
            } finally {
                if (backgroundTasksTestListener != null) {
                    backgroundTasksTestListener.onTerminated(CustomRxPresenter.this.getClass(), restartableId);
                }
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onCreate(Bundle savedState) {
        if (savedState != null)
            requested.addAll(savedState.getIntegerArrayList(REQUESTED_KEY));
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onDestroy() {
        views.onCompleted();
        subscriptions.unsubscribe();
        for (Map.Entry<Integer, Subscription> entry : restartableSubscriptions.entrySet())
            entry.getValue().unsubscribe();
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onSave(Bundle state) {
        for (int i = requested.size() - 1; i >= 0; i--) {
            int restartableId = requested.get(i);
            Subscription subscription = restartableSubscriptions.get(restartableId);
            if (subscription != null && subscription.isUnsubscribed())
                requested.remove(i);
        }
        state.putIntegerArrayList(REQUESTED_KEY, requested);
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onTakeView(View view) {
        views.onNext(view);
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onDropView() {
        views.onNext(null);
    }

    /**
     * Please, use restartableXX and deliverXX methods for pushing data from RxPresenter into View.
     */
    @Deprecated
    @Nullable
    @Override
    public View getView() {
        return super.getView();
    }

    public interface Function<View> {
        void call(View view);
    }

    @VisibleForTesting
    public interface BackgroundTasksTestListener {
        void waitIfNeeded(Class presenterClass, int id);

        void onTerminated(Class presenterClass, int id);
    }
}

