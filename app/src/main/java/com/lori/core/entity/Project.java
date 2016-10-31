package com.lori.core.entity;

import java.util.List;

/**
 * @author artemik
 */
public class Project extends BaseEntity {
    private String name;
    private List<Task> tasks;
    private List<ActivityType> activityTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<ActivityType> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<ActivityType> activityTypes) {
        this.activityTypes = activityTypes;
    }
}
