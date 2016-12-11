package com.lori.ui.presenter;

import android.os.Bundle;
import android.util.Log;
import com.lori.R;
import com.lori.core.entity.ActivityType;
import com.lori.core.entity.Project;
import com.lori.core.entity.Task;
import com.lori.core.entity.TimeEntry;
import com.lori.core.event.TimeEntryChangedEvent;
import com.lori.core.service.TimeEntryService;
import com.lori.core.service.exception.UserCancelledLoginException;
import com.lori.ui.base.BasePresenter;
import com.lori.ui.dialog.EditTimeEntryDialog;

import javax.inject.Inject;
import java.util.Calendar;

/**
 * @author artemik
 */
public class TimeEntryEditDialogPresenter extends BasePresenter<EditTimeEntryDialog> {
    private static final String TAG = TimeEntryEditDialogPresenter.class.getSimpleName();

    public static final int LOAD_AVAILABLE_PROJECTS = 0;
    public static final int COMMIT_TIME_ENTRY = 1;
    public static final int DELETE_TIME_ENTRY = 2;

    @Inject
    TimeEntryService timeEntryService;

    private Project chosenProject;
    private Task chosenTask;
    private ActivityType chosenActivityType;
    private int chosenHour = -1;
    private int chosenMinutes = -1;

    private Calendar dayDate;
    private TimeEntry timeEntryToEdit;
    private TimeEntry timeEntryToCommit;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        initLoadAvailableProjectsTask();
        initCommitTimeEntryTask();
        initDeleteTimeEntryTask();

        start(LOAD_AVAILABLE_PROJECTS);
    }

    private void initLoadAvailableProjectsTask() {
        restartableFirst(LOAD_AVAILABLE_PROJECTS,
                () -> timeEntryService.loadAvailableProjects(),
                (view, projects) -> {
                    view.setAvailableProjects(projects);
                    if (timeEntryToEdit != null) {

                        // The loaded available project contains all loaded tasks and activity types.
                        // Need to use it instead of timeEntryToEdit.task.project.
                        Project timeEntryToEditProject = null;
                        for (Project project : projects) {
                            if (project.getId().equals(timeEntryToEdit.getTask().getProject().getId())) {
                                timeEntryToEditProject = project;
                            }
                        }
                        view.setSelectedProject(timeEntryToEditProject);

                        view.setSelectedTask(timeEntryToEdit.getTask());

                        view.setSelectedActivityType(timeEntryToEdit.getActivityType());

                        int minutesSpent = timeEntryToEdit.getTimeInMinutes();
                        view.setSelectedHour(minutesSpent / 60);
                        view.setSelectedMinute(minutesSpent % 60);
                        view.setButtonsAreForEdit(true);
                    } else {
                        view.setSelectedHour(4);
                        view.setSelectedMinute(0);
                    }
                },
                (editTimeEntryDialog, throwable) -> {
                    Log.e(TAG, "Failed to load available projects when creating edit dialog", throwable);
                    if (throwable instanceof UserCancelledLoginException) {
                        editTimeEntryDialog.showToast(R.string.login_required);
                    } else {
                        editTimeEntryDialog.showNetworkError();
                    }
                });
    }

    private void initCommitTimeEntryTask() {
        restartableFirst(COMMIT_TIME_ENTRY,
                () -> (isEditing() ? timeEntryService.updatePersonalTimeEntry(timeEntryToCommit) : timeEntryService.savePersonalTimeEntry(timeEntryToCommit)),
                (dialog, timeEntry) -> {
                    dialog.setOnDismissListener(dialog1 -> {
                        if (isEditing()) {
                            eventBus.post(TimeEntryChangedEvent.changed(timeEntryToCommit, timeEntryToEdit));
                        } else {
                            eventBus.post(TimeEntryChangedEvent.added(timeEntryToCommit));
                        }
                    });
                    dialog.setConfirmButtonInProgress(false);
                    dialog.dismiss();
                },
                (dialog, throwable) -> {
                    Log.d(TAG, "Couldn't commit time entry: " + timeEntryToCommit);
                    if (throwable instanceof UserCancelledLoginException) {
                        dialog.showToast(R.string.login_required);
                    } else {
                        dialog.showNetworkError();
                    }
                    dialog.setButtonsAreActive(true);
                    dialog.setConfirmButtonInProgress(false);
                });
    }

    private void initDeleteTimeEntryTask() {
        restartableFirst(DELETE_TIME_ENTRY,
                () -> timeEntryService.removeTimeEntry(timeEntryToEdit),
                (dialog, removedTimeEntry) -> {
                    dialog.setOnDismissListener(dialog1 -> {
                        eventBus.post(TimeEntryChangedEvent.removed(timeEntryToEdit));
                    });
                    dialog.setDeleteButtonInProgress(false);
                    dialog.dismiss();
                },
                (dialog, throwable) -> {
                    Log.d(TAG, "Couldn't remove time entry: " + timeEntryToEdit);
                    if (throwable instanceof UserCancelledLoginException) {
                        dialog.showToast(R.string.login_required);
                    } else {
                        dialog.showNetworkError();
                    }
                    dialog.setButtonsAreActive(true);
                    dialog.setDeleteButtonInProgress(false);
                });
    }

    public void onProjectClick(Project project) {
        chosenProject = project;
    }

    public void onTaskClick(Task task) {
        chosenTask = task;
    }

    public void onActivityTypeClick(ActivityType activityType) {
        chosenActivityType = activityType;
    }

    public void onHourClick(int hour) {
        chosenHour = hour;
    }

    public void onMinutesClick(int minutes) {
        chosenMinutes = minutes;
    }

    public void onConfirmButtonClick() {
        getView().setButtonsAreActive(false);
        getView().setConfirmButtonInProgress(true);

        timeEntryToCommit = isEditing() ? getCopy(timeEntryToEdit) : new TimeEntry();
        timeEntryToCommit.setDate(dayDate.getTime());

        chosenTask.setProject(copyChosenProject()); // To throw away redundant fields.
        timeEntryToCommit.setTask(chosenTask);

        timeEntryToCommit.setActivityType(chosenActivityType);
        timeEntryToCommit.setTimeInMinutes(chosenHour * 60 + chosenMinutes);

        start(COMMIT_TIME_ENTRY);
    }

    private Project copyChosenProject() {
        Project copy = new Project();
        copy.setId(chosenProject.getId());
        copy.setName(chosenProject.getName());
        return copy;
    }

    private boolean isEditing() {
        return timeEntryToEdit != null;
    }

    private TimeEntry getCopy(TimeEntry timeEntry) {
        return new TimeEntry(timeEntry);
    }

    public void onDeleteButtonClick() {
        getView().setButtonsAreActive(false);
        getView().setDeleteButtonInProgress(true);
        start(DELETE_TIME_ENTRY);
    }

    public void setDayDate(Calendar dayDate) {
        this.dayDate = dayDate;
    }

    public void setTimeEntryToEdit(TimeEntry timeEntryToEdit) {
        this.timeEntryToEdit = timeEntryToEdit;
    }

    public TimeEntry getTimeEntryToEdit() {
        return timeEntryToEdit;
    }
}
