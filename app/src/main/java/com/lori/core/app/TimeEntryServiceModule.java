package com.lori.core.app;

import com.lori.core.gate.lori.LoriGate;
import com.lori.core.service.LoginService;
import com.lori.core.service.SessionService;
import com.lori.core.service.TimeEntryService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * @author artemik
 */
@Module
public class TimeEntryServiceModule {

    @Provides
    @Singleton
    public TimeEntryService timeEntryService(LoriGate loriGate, SessionService sessionService, LoginService loginService) {
        return new TimeEntryService(loriGate, sessionService, loginService);
    }
}
