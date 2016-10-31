package com.lori.core.service;

import com.lori.core.entity.Project;
import com.lori.core.entity.TimeEntry;
import com.lori.core.entity.User;
import com.lori.core.gate.lori.LoriGate;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Calendar;
import java.util.List;

/**
 * @author artemik
 */
@Singleton
public class TimeEntryService {

    @Inject
    LoriGate loriGate;

    @Inject
    SessionService sessionService;

    @Inject
    public TimeEntryService() {
    }

    public Observable<TimeEntry> saveTimeEntry(TimeEntry timeEntry) {
        return loriGate.commitTimeEntry(sessionService.getUser(), true, timeEntry)
                .subscribeOn(Schedulers.io())
                .flatMap((timeEntries) -> timeEntries == null || timeEntries.size() == 0 ?
                        null : Observable.just(timeEntries.get(0))
                );
    }

    public Observable<TimeEntry> updateTimeEntry(TimeEntry timeEntry) {
        //TODO: merge update and save method into one.
        return loriGate.commitTimeEntry(sessionService.getUser(), false, timeEntry)
                .subscribeOn(Schedulers.io())
                .flatMap((timeEntries) -> timeEntries == null || timeEntries.size() == 0 ?
                        null : Observable.just(timeEntries.get(0))
                );
    }

    public Observable<TimeEntry> removeTimeEntry(TimeEntry timeEntry) {
        return loriGate.removeTimeEntry(timeEntry)
                .subscribeOn(Schedulers.io())
                .flatMap((timeEntries) -> timeEntries == null || timeEntries.size() == 0 ?
                        null : Observable.just(timeEntries.get(0))
                );
    }

    public Observable<List<TimeEntry>> loadWeekTimeEntries(Calendar monday) {
        Calendar sunday = (Calendar) monday.clone();
        sunday.set(Calendar.DAY_OF_WEEK, 1); // Set to sunday.

        User user = sessionService.getUser();

        return loriGate.loadTimeEntries(monday.getTime(), sunday.getTime(), user);
    }

    public Observable<List<Project>> loadAvailableProjects() {
        return loriGate.loadAvailableProjects(sessionService.getUser())
                .subscribeOn(Schedulers.io());
    }
}
