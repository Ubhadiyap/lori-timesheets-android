package com.lori.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

import static com.lori.core.gate.lori.LoriGate.DATE_FORMAT;

/**
 * @author artemik
 */
public class TimeEntry extends BaseEntity {
    @JsonFormat(pattern = DATE_FORMAT)
    @JsonSerialize
    private Date date;
    private Task task;
    private ActivityType activityType;
    private Integer timeInMinutes;

    public TimeEntry() {
    }

    public Date getDate() {
        return date;
    }

    @JsonFormat(pattern = DATE_FORMAT)
    public void setDate(Date date) {
        this.date = date;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Integer getTimeInMinutes() {
        return timeInMinutes;
    }

    public void setTimeInMinutes(Integer timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
}
