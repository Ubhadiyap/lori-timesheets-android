package com.lori.core.gate.lori.dto;

import com.lori.core.entity.Project;
import com.lori.core.entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * @author artemik
 */
public class ProjectDto extends BaseEntityDto{
    private String name;
    private List<TaskDto> tasks;

    public ProjectDto() {
    }

    public ProjectDto(Project project) {
        super(project);
        name = project.getName();

        if (project.getTasks() != null) {
            tasks = new ArrayList<>();
            for (Task task : project.getTasks()) {
                tasks.add(new TaskDto(task));
            }
        }
    }

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

    @Override
    protected String getEntityClassName() {
        return "ts$Project";
    }
}
