package com.lori.core.service;

import com.lori.core.entity.Project;
import com.lori.core.entity.TimeEntry;
import com.lori.core.gate.lori.LoriGate;
import com.lori.core.util.Rx;
import rx.Observable;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

import java.util.Calendar;
import java.util.List;

/**
 * @author artemik
 */
public class TimeEntryService {

    LoriGate loriGate;
    SessionService sessionService;
    LoginService loginService;

    public TimeEntryService(LoriGate loriGate, SessionService sessionService, LoginService loginService) {
        this.loriGate = loriGate;
        this.sessionService = sessionService;
        this.loginService = loginService;
    }

    public Observable<TimeEntry> savePersonalTimeEntry(TimeEntry timeEntry) {
        timeEntry.setUser(sessionService.getUser());

        return withLoginIfBadSession(() ->
                loriGate.commitTimeEntries(true, timeEntry)
                        .subscribeOn(Schedulers.io())
                        .compose(loginService.reloginIfAuthenticationException())
                        .map(Rx::getFirst)
        );
    }

    public Observable<TimeEntry> updatePersonalTimeEntry(TimeEntry timeEntry) {
        //TODO: merge update and save method into one.

        timeEntry.setUser(sessionService.getUser());

        return withLoginIfBadSession(() ->
                loriGate.commitTimeEntries(false, timeEntry)
                        .subscribeOn(Schedulers.io())
                        .compose(loginService.reloginIfAuthenticationException())
                        .map(Rx::getFirst)
        );
    }

    public Observable<TimeEntry> removeTimeEntry(TimeEntry timeEntry) {
        return withLoginIfBadSession(() ->
                loriGate.removeTimeEntry(timeEntry)
                        .subscribeOn(Schedulers.io())
                        .map(Rx::getFirst)
        );
    }

    public Observable<List<TimeEntry>> loadPersonalWeekTimeEntries(Calendar monday) {
        Calendar sunday = (Calendar) monday.clone();
        sunday.set(Calendar.DAY_OF_WEEK, 1); // 1 - sunday ordinal.

        return withLoginIfBadSession(() ->
                loriGate.loadTimeEntries(monday.getTime(), sunday.getTime(), sessionService.getUser())
        );
    }

    public Observable<List<Project>> loadAvailableProjects() {
        return withLoginIfBadSession(() ->
                loriGate.loadAvailableProjects(sessionService.getUser())
                        .subscribeOn(Schedulers.io())
        );
    }

    private <T> Observable<T> withLoginIfBadSession(Func0<Observable<T>> observableFactory) {
        return loginService.withLoginIfBadSession(observableFactory);
    }
}
