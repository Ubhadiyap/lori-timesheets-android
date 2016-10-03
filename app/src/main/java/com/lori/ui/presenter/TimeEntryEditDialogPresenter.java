package com.lori.ui.presenter;

import android.os.Bundle;
import com.lori.core.entity.ActivityType;
import com.lori.core.entity.Project;
import com.lori.core.entity.Task;
import com.lori.core.entity.TimeEntry;
import com.lori.core.event.TimeEntryChangedEvent;
import com.lori.core.service.TimeEntryService;
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

        restartableReplay(LOAD_AVAILABLE_PROJECTS, () -> timeEntryService.loadAvailableProjects()
                        .observeOn(mainThread()),
                (editTimeEntryDialog, projects) -> {
                    editTimeEntryDialog.setAvailableProjects(projects);
                    if (timeEntryToEdit != null) {
                        editTimeEntryDialog.setSelectedProject(timeEntryToEdit.getProject());
                        editTimeEntryDialog.setSelectedTask(timeEntryToEdit.getTask());
                        editTimeEntryDialog.setSelectedActivityType(timeEntryToEdit.getActivityType());

                        int minutesSpent = timeEntryToEdit.getMinutesSpent();
                        editTimeEntryDialog.setSelectedHour(minutesSpent / 60);
                        editTimeEntryDialog.setSelectedMinute(minutesSpent % 60);
                        editTimeEntryDialog.setButtonForEdit(true);
                    } else {
                        editTimeEntryDialog.setSelectedHour(4);
                        editTimeEntryDialog.setSelectedMinute(0);
                    }
                },
                null);

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
        getView().setButtonsActive(false);

        TimeEntry previousTimeEntry = null;
        if (timeEntryToEdit == null) {
            timeEntryToEdit = new TimeEntry();
        } else {
            previousTimeEntry = new TimeEntry();
            previousTimeEntry.setDate(timeEntryToEdit.getDate());
            previousTimeEntry.setProject(timeEntryToEdit.getProject());
            previousTimeEntry.setTask(timeEntryToEdit.getTask());
            previousTimeEntry.setActivityType(timeEntryToEdit.getActivityType());
            previousTimeEntry.setMinutesSpent(timeEntryToEdit.getMinutesSpent());
        }

        timeEntryToEdit.setId(UUID.randomUUID());
        timeEntryToEdit.setDate(dayDate.getTime());
        timeEntryToEdit.setProject(chosenProject);
        timeEntryToEdit.setTask(chosenTask);
        timeEntryToEdit.setActivityType(chosenActivityType);
        timeEntryToEdit.setMinutesSpent(chosenHour * 60 + chosenMinutes);

        final TimeEntry finalPreviousTimeEntry = previousTimeEntry;

        add(timeEntryService.saveTimeEntry(timeEntryToEdit)
                .observeOn(mainThread())
                .subscribe(isSuccessful ->
                        view().filter(editTimeEntryDialog -> editTimeEntryDialog != null)
                                .take(1)
                                .subscribe(editTimeEntryDialog -> {
                                    editTimeEntryDialog.dismiss();
                                    editTimeEntryDialog.setOnDismissListener(dialog -> {
                                        if (finalPreviousTimeEntry != null) {
                                            eventBus.post(TimeEntryChangedEvent.changed(timeEntryToEdit, finalPreviousTimeEntry));
                                        } else {
                                            eventBus.post(TimeEntryChangedEvent.added(timeEntryToEdit));
                                        }
                                    });
                                }))
        );
    }

    public void onDeleteButtonClick() {
        getView().setButtonsActive(false);

        add(timeEntryService.deleteTimeEntry(timeEntryToEdit)
                .observeOn(mainThread())
                .subscribe(isSuccessful ->
                        view().filter(editTimeEntryDialog -> editTimeEntryDialog != null)
                                .take(1)
                                .subscribe(editTimeEntryDialog -> {
                                    editTimeEntryDialog.dismiss();
                                    editTimeEntryDialog.setOnDismissListener(dialog -> {
                                        eventBus.post(TimeEntryChangedEvent.removed(timeEntryToEdit));
                                    });
                                }))
        );
    }

    public void setDayDate(Calendar dayDate) {
        this.dayDate = dayDate;
    }

    public void setTimeEntryToEdit(TimeEntry timeEntryToEdit) {
        this.timeEntryToEdit = timeEntryToEdit;
    }
}
