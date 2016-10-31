package com.lori.core.gate.lori.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lori.core.entity.TimeEntry;
import com.lori.core.entity.User;
import com.lori.core.gate.lori.LoriGate;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author artemik
 */
public class TimeEntryDto extends BaseEntityDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LoriGate.DATE_FORMAT)
    private UserDto user;
    private Date date;
    private TaskDto task;
    private Integer timeInMinutes;
    private BigDecimal timeInHours;
    private ActivityTypeDto activityType;

    public TimeEntryDto() {
    }

    public TimeEntryDto(TimeEntry timeEntry) {
        super(timeEntry);
        date = timeEntry.getDate();
        task = new TaskDto(timeEntry.getTask());
        timeInMinutes = timeEntry.getTimeInMinutes();
        timeInHours = BigDecimal.valueOf(timeInMinutes / 60);
        activityType = new ActivityTypeDto(timeEntry.getActivityType());
    }

    public TimeEntryDto(User user, TimeEntry timeEntry) {
        this(timeEntry);
        this.user = new UserDto(user);
    }

    @Override
    protected String getEntityClassName() {
        return "ts$TimeEntry";
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LoriGate.DATE_FORMAT)
    public Date getDate() {
        return date;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LoriGate.DATE_FORMAT)
    public void setDate(Date date) {
        this.date = date;
    }

    public TaskDto getTask() {
        return task;
    }

    public void setTask(TaskDto task) {
        this.task = task;
    }

    public Integer getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(Integer timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public BigDecimal getTimeInHours() {
        return timeInHours;
    }

    public void setTimeInHours(BigDecimal timeInHours) {
        this.timeInHours = timeInHours;
    }

    public ActivityTypeDto getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityTypeDto activityType) {
        this.activityType = activityType;
    }
}
