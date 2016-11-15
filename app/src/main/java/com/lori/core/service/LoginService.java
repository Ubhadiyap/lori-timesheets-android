package com.lori.core.service;

import android.content.Intent;
import com.lori.core.app.App;
import com.lori.core.entity.User;
import com.lori.core.gate.lori.LoriGate;
import com.lori.core.gate.lori.exception.LoriAuthenticationException;
import com.lori.core.service.exception.UserCancelledLoginException;
import com.lori.ui.activity.LoginActivity;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author artemik
 */
@Singleton
public class LoginService {

    @Inject
    LoriGate loriGate;

    @Inject
    SessionService sessionService;

    @Inject
    App app;

    private static final PublishSubject<UUID> loginSubject = PublishSubject.create();
    private final AtomicBoolean loginActivityOpened = new AtomicBoolean(false);

    @Inject
    public LoginService() {
    }

    public Observable<User> login(String login, String password, String baseUrl) {
        loriGate.init(baseUrl);

        return loriGate.login(login, password)
                .doOnNext(session -> sessionService.setSession(session))
                .flatMap(receivedSession -> loriGate.loadUserByLogin(login))
                .doOnError(throwable -> sessionService.setSession(null)) // Reset session - can't consider it acquired if user loading failed.
                .doOnNext(user -> sessionService.setUser(user))
                .doOnNext(user -> sessionService.setServerUrl(baseUrl));
    }

    public void logout() {
        sessionService.clearSession();
        // TODO: call server logout.
    }

    public boolean isLoggedIn() {
        return sessionService.hasSession();
    }

    /**
     * Factory is required so that observable creation is not evaluated until a session exists.
     */
    public <T> Observable<T> withLoginIfBadSession(Func0<Observable<T>> observableFactory) {
        return Observable.fromCallable(() -> sessionService.getSessionEx())
                .compose(reloginIfAuthenticationException()) // Check any session exists.
                .flatMap(sessionId -> observableFactory.call())
                .compose(reloginIfAuthenticationException()); // Check if the session is not alive.
    }

    public <T> Observable.Transformer<T, T> reloginIfAuthenticationException() {
        return observable -> observable.retryWhen(errorObservable -> {
            return errorObservable.flatMap(new Func1<Throwable, Observable<?>>() {
                @Override
                public Observable<?> call(Throwable throwable) {
                    if (throwable instanceof LoriAuthenticationException) {
                        sessionService.clearSession();
                        startLoginActivityIfNotStarted();
                        return loginSubject.flatMap(sessionId -> sessionId == null ?
                                Observable.error(new UserCancelledLoginException(throwable)) :
                                Observable.just(sessionId)
                        );
                    } else {
                        return Observable.error(throwable);
                    }
                }
            });
        });
    }

    public void onLoginActivityClosed() {
        loginSubject.onNext(sessionService.getSession());
        loginActivityOpened.set(false);
    }

    public void startLoginActivityIfNotStarted() {
        if (loginActivityOpened.compareAndSet(false, true)) {
            Intent i = new Intent(app, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            app.startActivity(i);
        }
    }

    public Observable<UUID> getLoginSubject() {
        return loginSubject;
    }
}
