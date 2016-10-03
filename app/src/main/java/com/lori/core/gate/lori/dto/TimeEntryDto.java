package com.lori.core.gate.lori.dto;

import java.util.Date;

/**
 * @author artemik
 */
public class TimeEntryDto extends BaseEntityDto {
    private Date date;
    private ProjectDto project;
    private TaskDto task;
    private ActivityTypeDto activityType;
    private Integer minutesSpent;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    public TaskDto getTask() {
        return task;
    }

    public void setTask(TaskDto task) {
        this.task = task;
    }

    public ActivityTypeDto getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityTypeDto activityType) {
        this.activityType = activityType;
    }

    public Integer getMinutesSpent() {
        return minutesSpent;
    }

    public void setMinutesSpent(Integer minutesSpent) {
        this.minutesSpent = minutesSpent;
    }
}
