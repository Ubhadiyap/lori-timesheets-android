package com.lori.core.gate.lori.dto;

import java.util.List;

/**
 * @author artemik
 */
public class ProjectDto extends BaseEntityDto{
    private String name;
    private List<TaskDto> tasks;
    private List<ActivityTypeDto> activityTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TaskDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskDto> tasks) {
        this.tasks = tasks;
    }

    public List<ActivityTypeDto> getActivityTypes() {
        return activityTypes;
    }

    public void setActivityTypes(List<ActivityTypeDto> activityTypes) {
        this.activityTypes = activityTypes;
    }
}
