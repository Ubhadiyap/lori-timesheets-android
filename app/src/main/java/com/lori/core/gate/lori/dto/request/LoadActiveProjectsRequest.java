package com.lori.core.gate.lori.dto.request;

import com.lori.core.gate.lori.dto.UserDto;
import com.lori.core.gate.lori.dto.request.base.BaseServiceRequest;

/**
 * @author artemik
 */
public class LoadActiveProjectsRequest extends BaseServiceRequest {
    public LoadActiveProjectsRequest(UserDto user) {
        super(
                "ts_ProjectsService",
                "getActiveProjectsForUser",
                "project.withTasks",
                user, "project.withTasks");
    }
}
