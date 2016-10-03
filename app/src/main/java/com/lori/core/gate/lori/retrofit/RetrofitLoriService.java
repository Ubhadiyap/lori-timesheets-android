package com.lori.core.gate.lori.retrofit;

import com.lori.core.gate.lori.dto.ProjectDto;
import com.lori.core.gate.lori.dto.TimeEntryDto;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

import java.util.List;

/**
 * @author artemik
 */
public interface RetrofitLoriService {
    String PROJECT_PATH = "/projects";
    String TIME_ENTRIES_PATH = "/timeEntries";
    String SAVE_TIME_ENTRY_PATH = "/timeEntries/save";
    String DELETE_TIME_ENTRY_PATH = "/timeEntries/delete";

    @GET(PROJECT_PATH)
    Observable<List<ProjectDto>> loadAvailableProjects();

    @GET(TIME_ENTRIES_PATH)
    Observable<List<TimeEntryDto>> loadTimeEntries(@Query("startDate") Long startTimestamp, @Query("endDate") Long endTimestamp);

    @POST(SAVE_TIME_ENTRY_PATH)
    Observable<Response<Void>> saveTimeEntry(@Body TimeEntryDto timeEntry);

    @POST(DELETE_TIME_ENTRY_PATH)
    Observable<Response<Void>> deleteTimeEntry(@Body TimeEntryDto timeEntry);
}
