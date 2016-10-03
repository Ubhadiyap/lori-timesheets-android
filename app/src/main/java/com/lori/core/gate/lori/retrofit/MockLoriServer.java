package com.lori.core.gate.lori.retrofit;

import com.google.gson.Gson;
import com.lori.core.gate.lori.dto.ActivityTypeDto;
import com.lori.core.gate.lori.dto.ProjectDto;
import com.lori.core.gate.lori.dto.TaskDto;
import com.lori.core.gate.lori.dto.TimeEntryDto;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lori.core.gate.lori.retrofit.RetrofitLoriService.*;

/**
 * @author artemik
 */
public class MockLoriServer {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public static final String MOCK_BASE_URL = "http://localhost:9090/";

    private static ProjectDto cassandraProject;
    private static ProjectDto couchDbProject;
    private static ProjectDto felixProject;
    private static ProjectDto hadoopProject;
    private static ProjectDto jcloudsProject;
    private static ProjectDto stormProject;
    private static ProjectDto thriftProject;

    public static void setup() {
        EXECUTOR.submit(MockLoriServer::doSetup);
    }

    private static void doSetup() {
        MockWebServer server = new MockWebServer();

        initSampleData();

        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                //request.getBody().readUtf8()
                if (request.getPath().startsWith(PROJECT_PATH)) {
                    return new MockResponse().setResponseCode(200).setBody(createSampleProjectsJson());
                } else if (request.getPath().startsWith(TIME_ENTRIES_PATH)) {
                    return new MockResponse().setResponseCode(200).setBody(createSampleTimeEntriesJson());
                } else if (request.getPath().startsWith(SAVE_TIME_ENTRY_PATH)) {
                    return new MockResponse().setResponseCode(200);
                } else if (request.getPath().startsWith(DELETE_TIME_ENTRY_PATH)) {
                    return new MockResponse().setResponseCode(200);
                }
                return new MockResponse().setResponseCode(404);
            }
        };
        server.setDispatcher(dispatcher);
        try {
            server.start(9090);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initSampleData() {
        cassandraProject = createCassandraProject();
        couchDbProject = createCouchDbProject();
        felixProject = createFelixProject();
        hadoopProject = createHadoopProject();
        jcloudsProject = createJcloudsProject();
        stormProject = createStormProject();
        thriftProject = createThriftProject();
    }

    private static String createSampleTimeEntriesJson() {
        List<TimeEntryDto> timeEntries = createSampleTimeEntries();
        return new Gson().toJson(timeEntries);
    }

    private static List<TimeEntryDto> createSampleTimeEntries() {
        List<TimeEntryDto> timeEntries = new ArrayList<>();

        Calendar currentDate = Calendar.getInstance();

        currentDate.set(Calendar.DAY_OF_WEEK, 2); // Monday.
        List<TimeEntryDto> mondayEntries = createMondayEntries((Date) currentDate.getTime().clone());
        timeEntries.addAll(mondayEntries);

        currentDate.set(Calendar.DAY_OF_WEEK, 4);
        List<TimeEntryDto> wednesdayEntries = createWednesdayEntries((Date) currentDate.getTime().clone());
        timeEntries.addAll(wednesdayEntries);

        return timeEntries;
    }

    private static List<TimeEntryDto> createMondayEntries(Date date) {
        List<TimeEntryDto> entries = new ArrayList<>();

        TimeEntryDto entry1 = new TimeEntryDto();
        entry1.setId(UUID.randomUUID());
        entry1.setDate(date);
        entry1.setProject(cassandraProject);
        entry1.setTask(cassandraProject.getTasks().get(0));
        entry1.setActivityType(cassandraProject.getActivityTypes().get(0));
        entry1.setMinutesSpent(2 * 60 + 15);
        entries.add(entry1);

        TimeEntryDto entry2 = new TimeEntryDto();
        entry2.setId(UUID.randomUUID());
        entry2.setDate(date);
        entry2.setProject(cassandraProject);
        entry2.setTask(cassandraProject.getTasks().get(4));
        entry2.setActivityType(cassandraProject.getActivityTypes().get(0));
        entry2.setMinutesSpent(60);
        entries.add(entry2);

        return entries;
    }

    private static List<TimeEntryDto> createWednesdayEntries(Date date) {
        List<TimeEntryDto> entries = new ArrayList<>();

        TimeEntryDto entry1 = new TimeEntryDto();
        entry1.setId(UUID.randomUUID());
        entry1.setDate(date);
        entry1.setProject(hadoopProject);
        entry1.setTask(hadoopProject.getTasks().get(0));
        entry1.setActivityType(hadoopProject.getActivityTypes().get(4));
        entry1.setMinutesSpent(4 * 60);
        entries.add(entry1);

        TimeEntryDto entry2 = new TimeEntryDto();
        entry2.setId(UUID.randomUUID());
        entry2.setDate(date);
        entry2.setProject(hadoopProject);
        entry2.setTask(hadoopProject.getTasks().get(2));
        entry2.setActivityType(hadoopProject.getActivityTypes().get(4));
        entry2.setMinutesSpent(3 * 60 + 15);
        entries.add(entry2);

        TimeEntryDto entry3 = new TimeEntryDto();
        entry3.setId(UUID.randomUUID());
        entry3.setDate(date);
        entry3.setProject(hadoopProject);
        entry3.setTask(hadoopProject.getTasks().get(3));
        entry3.setActivityType(hadoopProject.getActivityTypes().get(4));
        entry3.setMinutesSpent(45);
        entries.add(entry3);

        return entries;
    }

    private static String createSampleProjectsJson() {
        List<ProjectDto> sampleProjects = createSampleProjects();

        Gson gson = new Gson();
        return gson.toJson(sampleProjects);
    }

    private static List<ProjectDto> createSampleProjects() {
        List<ProjectDto> projects = new ArrayList<>();
        projects.add(cassandraProject);
        projects.add(couchDbProject);
        projects.add(felixProject);
        projects.add(hadoopProject);
        projects.add(jcloudsProject);
        projects.add(stormProject);
        projects.add(thriftProject);
        return projects;
    }

    private static ProjectDto createCassandraProject() {
        ProjectDto project = new ProjectDto();
        project.setId(UUID.randomUUID());
        project.setName("Cassandra");

        List<TaskDto> tasks = new ArrayList<>();
        tasks.add(createTask("Compaction"));
        tasks.add(createTask("Configuration"));
        tasks.add(createTask("Coordination"));
        tasks.add(createTask("Coordination"));
        tasks.add(createTask("CQL"));
        tasks.add(createTask("Distributed Metadata"));
        tasks.add(createTask("Lifecycle"));
        tasks.add(createTask("Streaming"));
        tasks.add(createTask("Tools"));
        project.setTasks(tasks);

        List<ActivityTypeDto> activityTypes = new ArrayList<>();
        activityTypes.add(createActivityType("Analysis"));
        activityTypes.add(createActivityType("Development"));
        activityTypes.add(createActivityType("Documentation"));
        activityTypes.add(createActivityType("Support"));
        activityTypes.add(createActivityType("Testing"));
        project.setActivityTypes(activityTypes);

        return project;
    }

    private static ProjectDto createCouchDbProject() {
        ProjectDto project = new ProjectDto();
        project.setId(UUID.randomUUID());
        project.setName("Couch DB");

        List<TaskDto> tasks = new ArrayList<>();
        tasks.add(createTask("Database Core"));
        tasks.add(createTask("Fauxton"));
        tasks.add(createTask("Futon"));
        tasks.add(createTask("Mango"));
        tasks.add(createTask("Plugins"));
        tasks.add(createTask("View Server Support"));
        project.setTasks(tasks);

        List<ActivityTypeDto> activityTypes = new ArrayList<>();
        activityTypes.add(createActivityType("Analysis"));
        activityTypes.add(createActivityType("Development"));
        activityTypes.add(createActivityType("Documentation"));
        activityTypes.add(createActivityType("Support"));
        activityTypes.add(createActivityType("Testing"));
        project.setActivityTypes(activityTypes);

        return project;
    }

    private static ProjectDto createFelixProject() {
        ProjectDto project = new ProjectDto();
        project.setId(UUID.randomUUID());
        project.setName("Felix");

        List<TaskDto> tasks = new ArrayList<>();
        tasks.add(createTask("Bundle Repository"));
        tasks.add(createTask("Configurator"));
        tasks.add(createTask("Connect"));
        tasks.add(createTask("Converter"));
        tasks.add(createTask("Dependency Manager"));
        tasks.add(createTask("HTTP Service"));
        tasks.add(createTask("Installer"));
        tasks.add(createTask("Inventory"));
        tasks.add(createTask("iPOJO"));
        tasks.add(createTask("Main"));
        tasks.add(createTask("Remote Shell"));
        tasks.add(createTask("Sandbox"));
        tasks.add(createTask("User Admin"));
        project.setTasks(tasks);

        List<ActivityTypeDto> activityTypes = new ArrayList<>();
        activityTypes.add(createActivityType("Analysis"));
        activityTypes.add(createActivityType("Development"));
        activityTypes.add(createActivityType("Documentation"));
        activityTypes.add(createActivityType("Support"));
        activityTypes.add(createActivityType("Testing"));
        project.setActivityTypes(activityTypes);

        return project;
    }

    private static ProjectDto createHadoopProject() {
        ProjectDto project = new ProjectDto();
        project.setId(UUID.randomUUID());
        project.setName("Hadoop");

        List<TaskDto> tasks = new ArrayList<>();
        tasks.add(createTask("Benchmarks"));
        tasks.add(createTask("Metrics"));
        tasks.add(createTask("Native"));
        tasks.add(createTask("Perfomance"));
        tasks.add(createTask("Scripts"));
        tasks.add(createTask("Security"));
        tasks.add(createTask("Yetus"));
        project.setTasks(tasks);

        List<ActivityTypeDto> activityTypes = new ArrayList<>();
        activityTypes.add(createActivityType("Analysis"));
        activityTypes.add(createActivityType("Development"));
        activityTypes.add(createActivityType("Documentation"));
        activityTypes.add(createActivityType("Support"));
        activityTypes.add(createActivityType("Testing"));
        project.setActivityTypes(activityTypes);

        return project;
    }

    private static ProjectDto createJcloudsProject() {
        ProjectDto project = new ProjectDto();
        project.setId(UUID.randomUUID());
        project.setName("JClouds");

        List<TaskDto> tasks = new ArrayList<>();
        tasks.add(createTask("Blobstore"));
        tasks.add(createTask("Chef"));
        tasks.add(createTask("Cli"));
        tasks.add(createTask("Core"));
        tasks.add(createTask("Drivers"));
        tasks.add(createTask("Karaf"));
        tasks.add(createTask("Labs"));
        tasks.add(createTask("Openstack"));
        tasks.add(createTask("Loadbalancer"));
        project.setTasks(tasks);

        List<ActivityTypeDto> activityTypes = new ArrayList<>();
        activityTypes.add(createActivityType("Analysis"));
        activityTypes.add(createActivityType("Development"));
        activityTypes.add(createActivityType("Documentation"));
        activityTypes.add(createActivityType("Support"));
        activityTypes.add(createActivityType("Testing"));
        project.setActivityTypes(activityTypes);

        return project;
    }

    private static ProjectDto createStormProject() {
        ProjectDto project = new ProjectDto();
        project.setId(UUID.randomUUID());
        project.setName("Storm");

        List<TaskDto> tasks = new ArrayList<>();
        tasks.add(createTask("Asf Site"));
        tasks.add(createTask("Build"));
        tasks.add(createTask("Flux"));
        tasks.add(createTask("Core"));
        tasks.add(createTask("Eventhubs"));
        tasks.add(createTask("Jdbc"));
        tasks.add(createTask("Kinesis"));
        tasks.add(createTask("Ui"));
        tasks.add(createTask("Trident"));
        project.setTasks(tasks);

        List<ActivityTypeDto> activityTypes = new ArrayList<>();
        activityTypes.add(createActivityType("Analysis"));
        activityTypes.add(createActivityType("Development"));
        activityTypes.add(createActivityType("Documentation"));
        activityTypes.add(createActivityType("Support"));
        activityTypes.add(createActivityType("Testing"));
        project.setActivityTypes(activityTypes);

        return project;
    }

    private static ProjectDto createThriftProject() {
        ProjectDto project = new ProjectDto();
        project.setId(UUID.randomUUID());
        project.setName("Thrift");

        List<TaskDto> tasks = new ArrayList<>();
        tasks.add(createTask("Cocoa"));
        tasks.add(createTask("Compiler"));
        tasks.add(createTask("Library"));
        tasks.add(createTask("Swift"));
        tasks.add(createTask("Website"));
        project.setTasks(tasks);

        List<ActivityTypeDto> activityTypes = new ArrayList<>();
        activityTypes.add(createActivityType("Analysis"));
        activityTypes.add(createActivityType("Development"));
        activityTypes.add(createActivityType("Documentation"));
        activityTypes.add(createActivityType("Support"));
        activityTypes.add(createActivityType("Testing"));
        project.setActivityTypes(activityTypes);

        return project;
    }

    private static TaskDto createTask(String name) {
        TaskDto task = new TaskDto();
        task.setId(UUID.randomUUID());
        task.setName(name);
        return task;
    }

    private static ActivityTypeDto createActivityType(String name) {
        ActivityTypeDto type = new ActivityTypeDto();
        type.setId(UUID.randomUUID());
        type.setName(name);
        return type;
    }
}
