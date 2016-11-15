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
import java.util.UUID;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author artemik
 */
public class TimeEntryEditDialogPresenter extends BasePresenter<EditTimeEntryDialog> {
    private static final String TAG = TimeEntryEditDialogPresenter.class.getSimpleName();

    private static final int LOAD_AVAILABLE_PROJECTS = 0;

    @Inject
    TimeEntryService timeEntryService;

    private Project chosenProject;
    private Task chosenTask;
    private ActivityType chosenActivityType;
    private int chosenHour = -1;
    private int chosenMinutes = -1;

    private Calendar dayDate;
    private TimeEntry timeEntryToEdit;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableFirstAsync(LOAD_AVAILABLE_PROJECTS,
                () -> timeEntryService.loadAvailableProjects()
                        .observeOn(mainThread()),
                (view, projects) -> {
                    view.setAvailableProjects(projects);
                    if (timeEntryToEdit != null) {

                        // Available project contains all loaded tasks and activity types, need to use it,
                        // instead of timeEntryToEdit.task.project.
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

        start(LOAD_AVAILABLE_PROJECTS);
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

        final TimeEntry beforeCommitTimeEntryState = isEditing() ? getCopy(timeEntryToEdit) : null;

        TimeEntry timeEntryToCommit = isEditing() ? timeEntryToEdit : new TimeEntry();
        if (!isEditing()) {
            timeEntryToCommit.setId(UUID.randomUUID());
        }
        timeEntryToCommit.setDate(dayDate.getTime());

        chosenTask.setProject(copyChosenProject());
        timeEntryToCommit.setTask(chosenTask);

        timeEntryToCommit.setActivityType(chosenActivityType);
        timeEntryToCommit.setTimeInMinutes(chosenHour * 60 + chosenMinutes);

        first(() -> (isEditing() ? timeEntryService.updatePersonalTimeEntry(timeEntryToCommit) : timeEntryService.savePersonalTimeEntry(timeEntryToCommit))
                        .observeOn(mainThread()),
                (dialog, timeEntry) -> {
                    dialog.setOnDismissListener(dialog1 -> {
                        if (isEditing()) {
                            eventBus.post(TimeEntryChangedEvent.changed(timeEntryToCommit, beforeCommitTimeEntryState));
                        } else {
                            eventBus.post(TimeEntryChangedEvent.added(timeEntryToCommit));
                        }
                    });
                    dialog.dismiss();
                },
                (dialog, throwable) -> {
                    Log.d(TAG, "Couldn't commit time entry: " + timeEntryToCommit);
                    if (throwable instanceof UserCancelledLoginException) {
                        dialog.showToast(R.string.login_required);
                    } else {
                        dialog.showNetworkError();
                    }
                });
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
        TimeEntry copy = new TimeEntry();
        copy.setDate(timeEntry.getDate());
        copy.setTask(timeEntry.getTask());
        copy.setActivityType(timeEntry.getActivityType());
        copy.setTimeInMinutes(timeEntry.getTimeInMinutes());
        return copy;
    }

    public void onDeleteButtonClick() {
        getView().setButtonsAreActive(false);

        first(() -> timeEntryService.removeTimeEntry(timeEntryToEdit)
                        .observeOn(mainThread()),
                (dialog, timeEntry) -> {
                    dialog.setOnDismissListener(dialog1 -> {
                        eventBus.post(TimeEntryChangedEvent.removed(timeEntryToEdit));
                    });
                    dialog.dismiss();
                },
                (dialog, throwable) -> {
                    Log.d(TAG, "Couldn't remove time entry: " + timeEntryToEdit);
                    if (throwable instanceof UserCancelledLoginException) {
                        dialog.showToast(R.string.login_required);
                    } else {
                        dialog.showNetworkError();
                    }
                });
    }

    public void setDayDate(Calendar dayDate) {
        this.dayDate = dayDate;
    }

    public void setTimeEntryToEdit(TimeEntry timeEntryToEdit) {
        this.timeEntryToEdit = timeEntryToEdit;
    }
}
