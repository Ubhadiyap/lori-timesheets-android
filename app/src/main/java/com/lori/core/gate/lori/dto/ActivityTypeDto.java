package com.lori.core.gate.lori.dto;

import com.lori.core.entity.ActivityType;
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
public class ActivityTypeDto extends BaseEntityDto {
    private String name;

    public ActivityTypeDto(ActivityType activityType) {
        super(activityType);
        name = activityType.getName();
    }

    @Override
    protected String getEntityClassName() {
        return "ts$ActivityType";
    }
}
