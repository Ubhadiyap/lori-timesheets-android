package com.lori.core.gate.lori.dto.request;

import com.lori.core.gate.lori.dto.ProjectDto;
import com.lori.core.gate.lori.dto.request.base.BaseServiceRequest;

/**
 * @author artemik
 */
public class LoadActivityTypesForProjectRequest extends BaseServiceRequest {
    public LoadActivityTypesForProjectRequest(ProjectDto project) {
        super(
                "ts_ProjectsService",
                "getActivityTypesForProject",
                "_local",
                project, "_local");
    }
}
