package com.lori.core.gate.lori.dto;

import com.lori.core.entity.ActivityType;

/**
 * @author artemik
 */
public class ActivityTypeDto extends BaseEntityDto {
    private String name;

    public ActivityTypeDto() {
    }

    public ActivityTypeDto(ActivityType activityType) {
        super(activityType);
        name = activityType.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    protected String getEntityClassName() {
        return "ts$ActivityType";
    }
}
