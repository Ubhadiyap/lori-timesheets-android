package com.lori.ui.presenter;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import com.lori.core.gate.lori.exception.LoriAuthenticationException;
import com.lori.core.service.LoginService;
import com.lori.ui.activity.LoginActivity;
import com.lori.ui.base.BasePresenter;
import hugo.weaving.DebugLog;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author artemik
 */
public class LoginActivityPresenter extends BasePresenter<LoginActivity> {
    private static final String TAG = LoginActivityPresenter.class.getSimpleName();

    @Inject
    LoginService loginService;

    public static final int LOGIN_REQUEST = 0;

    private String typedLogin;
    private String typedPassword;
    private String typedServerUrl;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirst(LOGIN_REQUEST,
                () -> loginService.login(typedLogin, typedPassword, typedServerUrl)
                        .observeOn(mainThread()),
                (loginActivity, user) -> loginActivity.onBackPressed(),
                (loginActivity, throwable) -> {
                    if (throwable instanceof LoriAuthenticationException) {
                        loginActivity.showCredentialsAreIncorrect();
                    } else {
                        Log.e(TAG, "Couldn't login", throwable);
                        loginActivity.showNetworkError();
                    }
                    loginActivity.setSignInButtonEnabled(true);
                });
    }

    @DebugLog
    public void onSignInButtonClick() {
        String login = getView().getLoginInputText();
        if (!isLoginValid(login)) {
            getView().showLoginIsInvalid();
            return;
        }
        typedLogin = login;

        String password = getView().getPasswordInputText();
        if (!isPasswordValid(password)) {
            getView().showPasswordIsInvalid();
            return;
        }
        typedPassword = password;

        String serverUrl = getView().getServerUrlInputText();
        if (!isServerUrlValid(serverUrl)) {
            getView().showServerUrlIsInvalid();
            return;
        }
        typedServerUrl = serverUrl;

        getView().setSignInButtonEnabled(false);
        start(LOGIN_REQUEST);
    }

    private boolean isLoginValid(String login) {
        return StringUtils.isNotBlank(login);
    }

    private boolean isPasswordValid(String password) {
        return StringUtils.isNotBlank(password);
    }

    private boolean isServerUrlValid(String serverUrl) {
        if (serverUrl != null) {
            serverUrl = serverUrl.replace("localhost:", "127.0.0.1:"); //TODO: Make more universal check.
        }
        return Patterns.WEB_URL.matcher(serverUrl).matches();
    }

    public boolean onBackPressed() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginService.onLoginActivityClosed();
    }
}
