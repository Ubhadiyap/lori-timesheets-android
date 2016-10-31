package com.lori.core.service;

import com.lori.core.entity.User;
import com.lori.core.gate.lori.LoriGate;
import com.lori.core.gate.lori.exception.LoriAuthenticationException;
import com.lori.core.service.exception.AuthenticationException;
import rx.Observable;

import javax.inject.Inject;

/**
 * @author artemik
 */
public class LoginService {

    @Inject
    LoriGate loriGate;

    @Inject
    SessionService sessionService;

    @Inject
    public LoginService() {
    }

    public Observable<User> login(String login, String password, String baseUrl) {
        loriGate.init(baseUrl);

        return loriGate.login(login, password)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof LoriAuthenticationException) {
                        return Observable.error(new AuthenticationException(throwable));
                    } else {
                        return Observable.error(throwable);
                    }
                })
                .doOnNext(session -> sessionService.setSession(session))
                .flatMap(receivedSession -> loriGate.loadUserByLogin(login))
                .doOnError(throwable -> sessionService.setSession(null)) // Reset session - can't consider it acquired if user loading failed.
                .doOnNext(user -> sessionService.setUser(user))
                .doOnNext(user -> sessionService.setServerUrl(baseUrl));
    }
}
