package com.lori.core.gate.lori.dto;

import com.lori.core.entity.Task;

/**
 * @author artemik
 */
public class TaskDto extends BaseEntityDto {
    private String name;
    private ProjectDto project;

    public TaskDto() {
    }

    public TaskDto(Task task) {
        super(task);
        name = task.getName();
        project = new ProjectDto(task.getProject());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProjectDto getProject() {
        return project;
    }

    public void setProject(ProjectDto project) {
        this.project = project;
    }

    @Override
    protected String getEntityClassName() {
        return "ts$Task";
    }
}
