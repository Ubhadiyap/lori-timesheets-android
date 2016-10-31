package com.lori.core.gate.lori.retrofit;

import com.lori.core.gate.lori.dto.ActivityTypeDto;
import com.lori.core.gate.lori.dto.ProjectDto;
import com.lori.core.gate.lori.dto.TimeEntryDto;
import com.lori.core.gate.lori.dto.UserDto;
import com.lori.core.gate.lori.dto.request.LoadActiveProjectsRequest;
import com.lori.core.gate.lori.dto.request.LoadActivityTypesForProjectRequest;
import com.lori.core.gate.lori.dto.request.LoadUserByLoginRequest;
import com.lori.core.gate.lori.dto.request.base.CommitRequest;
import com.lori.core.gate.lori.dto.request.base.ServiceResponse;
import retrofit2.http.*;
import rx.Observable;

import java.util.List;
import java.util.Map;

/**
 * @author artemik
 */
public interface RetrofitLoriService {
    String LOGIN_PATH = "login";
    String QUERY_PATH = "query.json";
    String SERVICE_PATH = "service.json";
    String COMMIT_PATH = "commit.json";
    String PROJECT_PATH = "projects";

    @GET(LOGIN_PATH)
    Observable<String> login(@Query("u") String login, @Query("p") String password);

    @POST(QUERY_PATH)
    Observable<List<UserDto>> loadUsersByLogin(@Body LoadUserByLoginRequest request);

    @GET(QUERY_PATH)
    Observable<List<TimeEntryDto>> executeQuery(@Query("e") String entity, @Query("q") String query, @QueryMap Map<String, Object> parameters,
                                                @Query("view") String view);

    @POST(SERVICE_PATH)
    Observable<ServiceResponse<List<ProjectDto>>> loadActiveProjects(@Body LoadActiveProjectsRequest request);

    @POST(COMMIT_PATH)
    Observable<List<TimeEntryDto>> commitTimeEntry(@Body CommitRequest<TimeEntryDto> request);

    @POST(SERVICE_PATH)
    Observable<ServiceResponse<List<ActivityTypeDto>>> loadActivityTypesForProject(@Body LoadActivityTypesForProjectRequest request);
}
