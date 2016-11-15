package com.lori.ui.activity;

import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import com.lori.BuildConfig;
import com.lori.R;
import com.lori.ui.UiUtils;
import com.lori.ui.base.BaseActivity;
import com.lori.ui.presenter.LoginActivityPresenter;
import nucleus.factory.RequiresPresenter;

/**
 * @author artemik
 */
@RequiresPresenter(LoginActivityPresenter.class)
public class LoginActivity extends BaseActivity<LoginActivityPresenter> {

    @BindView(R.id.loginInputText)
    EditText loginInputText;

    @BindView(R.id.passwordInputText)
    EditText passwordInputText;

    @BindView(R.id.serverUrlInputText)
    EditText serverUrlInputText;

    @BindView(R.id.signInButton)
    Button signInButton;

    @Override
    protected Integer getContentViewId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onContentViewSet() {
        loginInputText.setText(BuildConfig.DEFAULT_LOGIN);
        loginInputText.requestFocus();

        passwordInputText.setText(BuildConfig.DEFAULT_PASSWORD);

        resetServerUrlInputText();
    }

    private void resetServerUrlInputText() {
        serverUrlInputText.setText(BuildConfig.DEFAULT_SERVER_URL);
    }

    @OnClick(R.id.signInButton)
    void onSignInButtonClick() {
        getPresenter().onSignInButtonClick();
    }

    @OnClick(R.id.serverUrlResetButton)
    void onServerUrlResetButtonClick() {
        resetServerUrlInputText();
    }

    public void showLoginIsInvalid() {
        loginInputText.setError(getString(R.string.error_invalid_login));
        loginInputText.requestFocus();
    }

    public void showPasswordIsInvalid() {
        passwordInputText.setError(getString(R.string.error_invalid_password));
        passwordInputText.requestFocus();
    }

    public void showServerUrlIsInvalid() {
        serverUrlInputText.setError(getString(R.string.error_invalid_server_url));
        serverUrlInputText.requestFocus();
    }

    public void showCredentialsAreIncorrect() {
        passwordInputText.setError(getString(R.string.error_incorrect_credentials));
        passwordInputText.requestFocus();
    }

    public String getLoginInputText() {
        return loginInputText.getText().toString();
    }

    public String getPasswordInputText() {
        return passwordInputText.getText().toString();
    }

    public String getServerUrlInputText() {
        return serverUrlInputText.getText().toString();
    }

    public void setSignInButtonEnabled(boolean enabled) {
        UiUtils.setButtonEnabled(signInButton, enabled);
    }

    @Override
    public void onBackPressed() {
        if (getPresenter().onBackPressed()) {
            super.onBackPressed();
        }
    }
}

