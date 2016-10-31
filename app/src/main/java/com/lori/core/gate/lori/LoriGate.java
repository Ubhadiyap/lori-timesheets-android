package com.lori.core.gate.lori;

import android.support.v4.util.Pair;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lori.core.entity.*;
import com.lori.core.gate.lori.dto.*;
import com.lori.core.gate.lori.dto.request.LoadActiveProjectsRequest;
import com.lori.core.gate.lori.dto.request.LoadActivityTypesForProjectRequest;
import com.lori.core.gate.lori.dto.request.LoadUserByLoginRequest;
import com.lori.core.gate.lori.dto.request.base.CommitRequest;
import com.lori.core.gate.lori.dto.request.base.ServiceResponse;
import com.lori.core.gate.lori.exception.LoriAuthenticationException;
import com.lori.core.gate.lori.retrofit.RetrofitLoriService;
import com.lori.core.gate.lori.retrofit.RetrofitLoriServiceFactory;
import com.lori.core.service.SessionService;
import org.apache.commons.lang3.ObjectUtils;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author artemik
 */
@Singleton
public class LoriGate {

    @Inject
    RetrofitLoriServiceFactory retrofitLoriServiceFactory;

    SessionService sessionService;

    private RetrofitLoriService retrofitLoriService;

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final SimpleDateFormat DATE_TIME_FORMAT_INSTANCE = new SimpleDateFormat(DATE_TIME_FORMAT);

    @Inject
    public LoriGate() {
    }

    @Inject
    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;

        String savedServerUrl = sessionService.getServerUrl();
        if (savedServerUrl != null) {
            init(sessionService.getServerUrl());
        }

    }

    public void init(String baseUrl) {
        retrofitLoriService = retrofitLoriServiceFactory.create(baseUrl);
    }

    public Observable<UUID> login(String login, String password) {
        return retrofitLoriService.login(login, password)
                .subscribeOn(Schedulers.io())
                .map(UUID::fromString)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof HttpException &&
                            ((HttpException) throwable).code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        return Observable.error(new LoriAuthenticationException(throwable));
                    } else {
                        return Observable.error(throwable);
                    }
                });
    }

    public Observable<User> loadUserByLogin(String login) {
        LoadUserByLoginRequest request = new LoadUserByLoginRequest(login);

        return retrofitLoriService.loadUsersByLogin(request)
                .subscribeOn(Schedulers.io())
                .map(users -> users.isEmpty() ? null : users.get(0))
                .map(this::convertUser);
    }

    public Observable<List<Project>> loadAvailableProjects(User user) {
        UserDto userDto = new UserDto(user);

        return retrofitLoriService.loadActiveProjects(new LoadActiveProjectsRequest(userDto))
                .subscribeOn(Schedulers.io())
                .map(ServiceResponse::getResult)
                .flatMap(Observable::from)
                .flatMap(
                        (projectDto) -> retrofitLoriService.loadActivityTypesForProject(
                                new LoadActivityTypesForProjectRequest(projectDto)
                        ).map(ServiceResponse::getResult),
                        Pair::new
                )
                .sorted((pair, pair2) -> ObjectUtils.compare(pair.first.getName(), pair2.first.getName()))
                .map(pair -> convertProject(pair.first, pair.second))
                .toList();
    }

    public Observable<List<TimeEntry>> commitTimeEntry(User user, boolean isNew, TimeEntry... commitInstances) {
        // TODO: make universal commit() method working for any entity.

        if (commitInstances == null || commitInstances.length == 0) {
            return Observable.empty();
        }

        List<TimeEntryDto> timeEntryDtos = new ArrayList<>();
        for (TimeEntry commitInstance : commitInstances) {
            TimeEntryDto timeEntryDto = new TimeEntryDto(user, commitInstance);
            timeEntryDto.setNew(isNew);
            timeEntryDto.getTask().setProject(null);
            timeEntryDtos.add(timeEntryDto);
        }

        return retrofitLoriService.commitTimeEntry(new CommitRequest<>(timeEntryDtos))
                .map(this::convertTimeEntries)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<TimeEntry>> removeTimeEntry(TimeEntry timeEntry) {
        // TODO: make universal remove() method working for any entity.

        List<TimeEntryDto> removeInstances = new ArrayList<>();
        TimeEntryDto timeEntryDto = new TimeEntryDto();
        timeEntryDto.setId(timeEntry.getId());
        removeInstances.add(timeEntryDto);

        return retrofitLoriService.commitTimeEntry(new CommitRequest<>(null, removeInstances))
                .onErrorResumeNext((throwable) -> Observable.just(Lists.newArrayList(timeEntryDto))) // TODO: now it ignores error. Remove it when server's fixed.
                .map(this::convertTimeEntries)
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<TimeEntry>> loadTimeEntries(Date start, Date end, User user) {
        /*UserDto userDto = new UserDto(user);
        LoadTimeEntriesQueryRequest request = new LoadTimeEntriesQueryRequest(start, end, userDto);*/
        // TODO: Switch to service invocation instead of manual query, when server bug gets fixed, or at least to jpql post method.
        // TODO: #PL-8117, #PL-8118.

        return retrofitLoriService.executeQuery(
                "ts$TimeEntry",
                "select te from ts$TimeEntry te where te.user.id = :userId and (te.date between :start and :end)",
                ImmutableMap.of(
                        "userId", user.getId(),
                        "start", DATE_TIME_FORMAT_INSTANCE.format(start),
                        "end", DATE_TIME_FORMAT_INSTANCE.format(end)
                ),
                "timeEntry.dayDisplay"
        ).subscribeOn(Schedulers.io())
                .map(this::convertTimeEntries);
    }

    private User convertUser(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        User user = new User();
        user.setId(userDto.getId());
        user.setLogin(userDto.getLogin());
        return user;
    }

    private List<TimeEntry> convertTimeEntries(List<TimeEntryDto> timeEntryDtos) {
        List<TimeEntry> timeEntries = new ArrayList<>();

        if (timeEntryDtos != null && !timeEntryDtos.isEmpty()) {
            for (TimeEntryDto timeEntryDto : timeEntryDtos) {
                timeEntries.add(convertTimeEntry(timeEntryDto));
            }
        }

        return timeEntries;
    }

    private TimeEntry convertTimeEntry(TimeEntryDto timeEntryDto) {
        if (timeEntryDto == null) {
            return null;
        }

        TimeEntry timeEntry = new TimeEntry();
        timeEntry.setId(timeEntryDto.getId());
        timeEntry.setDate(timeEntryDto.getDate());
        timeEntry.setTask(convertTask(timeEntryDto.getTask()));
        timeEntry.setActivityType(convertActivityType(timeEntryDto.getActivityType()));
        timeEntry.setTimeInMinutes(timeEntryDto.getTimeInMinutes());
        return timeEntry;
    }

    private List<ActivityType> convertActivityTypes(List<ActivityTypeDto> activityTypeDtos) {
        List<ActivityType> activityTypes = new ArrayList<>();

        if (activityTypeDtos != null && !activityTypeDtos.isEmpty()) {
            for (ActivityTypeDto activityTypeDto : activityTypeDtos) {
                activityTypes.add(convertActivityType(activityTypeDto));
            }
        }

        return activityTypes;
    }

    private ActivityType convertActivityType(ActivityTypeDto activityTypeDto) {
        if (activityTypeDto == null) {
            return null;
        }

        ActivityType activityType = new ActivityType();
        activityType.setId(activityTypeDto.getId());
        activityType.setName(activityTypeDto.getName());
        return activityType;
    }

    private List<Task> convertTasks(List<TaskDto> taskDtos) {
        List<Task> tasks = new ArrayList<>();

        if (taskDtos != null && !taskDtos.isEmpty()) {
            for (TaskDto taskDto : taskDtos) {
                tasks.add(convertTask(taskDto));
            }
        }

        return tasks;
    }

    private Task convertTask(TaskDto taskDto) {
        if (taskDto == null) {
            return null;
        }

        Task task = new Task();
        task.setId(taskDto.getId());
        task.setName(taskDto.getName());
        task.setProject(convertProject(taskDto.getProject(), null));
        return task;
    }

    private Project convertProject(ProjectDto projectDto, List<ActivityTypeDto> activityTypeDtos) {
        if (projectDto == null) {
            return null;
        }

        Project project = new Project();
        project.setId(projectDto.getId());
        project.setName(projectDto.getName());
        project.setTasks(convertTasks(projectDto.getTasks()));
        project.setActivityTypes(convertActivityTypes(activityTypeDtos));
        return project;
    }
}
