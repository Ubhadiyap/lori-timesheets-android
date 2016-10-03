package com.lori.core.service;

import com.lori.core.entity.Project;
import com.lori.core.entity.TimeEntry;
import com.lori.core.gate.lori.dto.ProjectDto;
import com.lori.core.gate.lori.dto.TimeEntryDto;
import com.lori.core.gate.lori.retrofit.RetrofitLoriService;
import com.lori.core.gate.lori.retrofit.RetrofitLoriServiceFactory;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import retrofit2.Response;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lori.core.gate.lori.retrofit.MockLoriServer.MOCK_BASE_URL;

/**
 * @author artemik
 */
public class TimeEntryService {

    private RetrofitLoriService retrofitLoriService = RetrofitLoriServiceFactory.create(MOCK_BASE_URL);
    private static final int NETWORK_FAKE_DELAY_MS = 600;

    public Observable<Boolean> saveTimeEntry(TimeEntry timeEntry) {
        TimeEntryDto timeEntryDto = new ModelMapper().map(timeEntry, TimeEntryDto.class);

        return retrofitLoriService.saveTimeEntry(timeEntryDto)
                .subscribeOn(Schedulers.io())
                .delay(NETWORK_FAKE_DELAY_MS, TimeUnit.MILLISECONDS)
                .map(Response::isSuccessful);
    }

    public Observable<Boolean> deleteTimeEntry(TimeEntry timeEntry) {
        TimeEntryDto timeEntryDto = new ModelMapper().map(timeEntry, TimeEntryDto.class);

        return retrofitLoriService.deleteTimeEntry(timeEntryDto)
                .subscribeOn(Schedulers.io())
                .delay(NETWORK_FAKE_DELAY_MS, TimeUnit.MILLISECONDS)
                .map(Response::isSuccessful);
    }

    public Observable<List<TimeEntry>> loadTimeEntries(Calendar firstDayOfWeek) {
        Calendar endDayOfWeek = (Calendar) firstDayOfWeek.clone();
        endDayOfWeek.set(Calendar.DAY_OF_WEEK, 1);

        return retrofitLoriService.loadTimeEntries(firstDayOfWeek.getTime().getTime(), endDayOfWeek.getTime().getTime())
                .subscribeOn(Schedulers.io())
                .delay(NETWORK_FAKE_DELAY_MS, TimeUnit.MILLISECONDS)
                .map(timeEntriesConversionFunction());
    }

    public Observable<List<Project>> loadAvailableProjects() {
        return retrofitLoriService.loadAvailableProjects()
                .subscribeOn(Schedulers.io())
                .delay(NETWORK_FAKE_DELAY_MS, TimeUnit.MILLISECONDS)
                .map(projectsConversionFunction());
    }

    private Func1<List<ProjectDto>, List<Project>> projectsConversionFunction() {
        return projectDtos -> new ModelMapper().map(projectDtos, new TypeToken<List<Project>>() {
        }.getType());
    }

    private Func1<List<TimeEntryDto>, List<TimeEntry>> timeEntriesConversionFunction() {
        return timeEntryDtos -> new ModelMapper().map(timeEntryDtos, new TypeToken<List<TimeEntry>>() {
        }.getType());
    }
}
