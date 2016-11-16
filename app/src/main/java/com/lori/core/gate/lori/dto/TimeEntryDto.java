package com.lori.core.gate.lori.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lori.core.entity.TimeEntry;
import com.lori.core.gate.lori.LoriGate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author artemik
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class TimeEntryDto extends BaseEntityDto {
    private UserDto user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = LoriGate.DATE_FORMAT)
    private Date date;

    private TaskDto task;
    private Integer timeInMinutes;
    private BigDecimal timeInHours;
    private ActivityTypeDto activityType;

    public TimeEntryDto(TimeEntry timeEntry, boolean forCommit, boolean isNew) {
        super(timeEntry);
        date = timeEntry.getDate();

        task = new TaskDto(timeEntry.getTask());
        if (forCommit) {
            task.setProject(null);
        }

        timeInMinutes = timeEntry.getTimeInMinutes();
        timeInHours = timeInMinutes == null ? null : BigDecimal.valueOf(timeInMinutes / 60);
        activityType = new ActivityTypeDto(timeEntry.getActivityType());
        user = new UserDto(timeEntry.getUser());
        this.isNew = isNew;
    }

    @Override
    protected String getEntityClassName() {
        return "ts$TimeEntry";
    }
}
