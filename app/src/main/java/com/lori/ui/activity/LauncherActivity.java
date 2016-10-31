package com.lori.ui.activity;

import android.content.Intent;
import com.lori.core.service.SessionService;
import com.lori.ui.base.BaseActivity;

import javax.inject.Inject;

/**
 * @author artemik
 */
public class LauncherActivity extends BaseActivity {

    @Inject
    SessionService sessionService;

    @Override
    protected void onContentViewSet() {
        inject();

        Intent intent = new Intent(this, sessionService.isAuthenticated() ?
                WeekActivity.class :
                LoginActivity.class
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
