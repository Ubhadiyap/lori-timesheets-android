package com.lori;

import com.lori.base.BaseTest;
import com.lori.core.entity.User;
import com.lori.core.gate.lori.exception.LoriAuthenticationException;
import com.lori.core.service.LoginService;
import com.lori.ui.activity.LoginActivity;
import com.lori.ui.presenter.LoginActivityPresenter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

import static com.lori.ui.presenter.LoginActivityPresenter.LOGIN_REQUEST;
import static org.mockito.Mockito.*;

/**
 * @author artemik
 */

public class LoginActivityPresenterTest extends BaseTest {

    private static final String VALID_LOGIN = "login";
    private static final String VALID_PASSWORD = "password";
    private static final String VALID_SERVER_URL = "http://127.0.0.1/";

    @Inject
    LoginService loginService;

    @Before
    public void before() {
        getApplication().inject(this);
    }

    @After
    public void tearDown() {
        super.tearDown();
        reset(loginService);
    }

    @Test
    public void loginButtonClicked_ValidInput_RequestLoginFromServer() {
        LoginActivity view = mock(LoginActivity.class);
        when(view.getLoginInputText()).thenReturn(VALID_LOGIN);
        when(view.getPasswordInputText()).thenReturn(VALID_PASSWORD);
        when(view.getServerUrlInputText()).thenReturn(VALID_SERVER_URL);

        when(loginService.login(VALID_LOGIN, VALID_PASSWORD, VALID_SERVER_URL)).thenReturn(
                Observable.just(new User())
                        .subscribeOn(Schedulers.io())
        );

        LoginActivityPresenter presenter = createPresenter(LoginActivityPresenter.class, view);

        onMainThreadBlocking(() -> {
            presenter.onSignInButtonClick();

            verify(view).setSignInButtonEnabled(false);
        });

        waitForCompletion(LoginActivityPresenter.class, LOGIN_REQUEST);

        onMainThreadBlocking(() -> {
            verify(view).onBackPressed();
        });
    }

    @Test
    public void loginButtonClicked_InvalidLogin_ShowInvalidLoginError() {
        LoginActivity view = mock(LoginActivity.class);
        when(view.getLoginInputText()).thenReturn(null);
        when(view.getPasswordInputText()).thenReturn(VALID_PASSWORD);
        when(view.getServerUrlInputText()).thenReturn(VALID_SERVER_URL);

        LoginActivityPresenter presenter = createPresenter(LoginActivityPresenter.class, view);

        onMainThreadBlocking(() -> {
            presenter.onSignInButtonClick();

            verify(view).showLoginIsInvalid();
            verify(view, never()).onBackPressed();
            verifyZeroInteractions(loginService);
        });
    }

    @Test
    public void loginButtonClicked_InvalidCredentials_ShowCredentialsAreIncorrect() {
        LoginActivity view = mock(LoginActivity.class);
        when(view.getLoginInputText()).thenReturn(VALID_LOGIN);
        when(view.getPasswordInputText()).thenReturn(VALID_PASSWORD);
        when(view.getServerUrlInputText()).thenReturn(VALID_SERVER_URL);

        when(loginService.login(VALID_LOGIN, VALID_PASSWORD, VALID_SERVER_URL)).thenReturn(
                Observable.<User>error(new LoriAuthenticationException())
                        .subscribeOn(Schedulers.io())
        );

        LoginActivityPresenter presenter = createPresenter(LoginActivityPresenter.class, view);

        onMainThreadBlocking(presenter::onSignInButtonClick);

        waitForCompletion(LoginActivityPresenter.class, LOGIN_REQUEST);

        onMainThreadBlocking(() -> {
            verify(view).showCredentialsAreIncorrect();
        });
    }
}
