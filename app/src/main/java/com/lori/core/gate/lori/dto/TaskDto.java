package com.lori.core.gate.lori.dto;

import com.lori.core.entity.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author artemik
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TaskDto extends BaseEntityDto {
    private String name;
    private ProjectDto project;

    public TaskDto(Task task) {
        super(task);
        name = task.getName();
        project = new ProjectDto(task.getProject());
    }

    @Override
    protected String getEntityClassName() {
        return "ts$Task";
    }
}
