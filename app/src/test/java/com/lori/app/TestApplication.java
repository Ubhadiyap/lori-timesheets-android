package com.lori.app;

import com.lori.core.app.*;
import com.lori.core.gate.lori.LoriGate;
import com.lori.core.service.LoginService;
import com.lori.core.service.SessionService;
import com.lori.core.service.TimeEntryService;

import static org.mockito.Mockito.mock;

/**
 * @author artemik
 */
public class TestApplication extends App {

    @Override
    protected AppComponent createAppComponent() {
        return DaggerTestAppComponent.builder()
                .appModule(new AppModule(this))
                .loginServiceModule(new LoginServiceModule() {
                    @Override
                    public LoginService timeEntryService(LoriGate loriGate, SessionService sessionService, App app) {
                        return mock(LoginService.class);
                    }
                })
                .timeEntryServiceModule(new TimeEntryServiceModule() {
                    @Override
                    public TimeEntryService timeEntryService(LoriGate loriGate, SessionService sessionService, LoginService loginService) {
                        return mock(TimeEntryService.class);
                    }
                })
                .build();
    }
}
