package com.lori.core.app;

import com.lori.core.gate.lori.LoriGate;
import com.lori.core.service.LoginService;
import com.lori.core.service.SessionService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author artemik
 */
@Module
public class LoginServiceModule {

    @Provides
    @Singleton
    public LoginService timeEntryService(LoriGate loriGate, SessionService sessionService, App app) {
        return new LoginService(loriGate, sessionService, app);
    }
}
