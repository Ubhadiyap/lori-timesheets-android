package com.lori.ui.dialog;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.balysv.materialripple.MaterialRippleLayout;
import com.lori.R;
import com.lori.core.entity.ActivityType;
import com.lori.core.entity.Project;
import com.lori.core.entity.Task;
import com.lori.core.entity.TimeEntry;
import com.lori.ui.UiUtils;
import com.lori.ui.adapter.ActivityTypeListAdapter;
import com.lori.ui.adapter.ProjectListAdapter;
import com.lori.ui.adapter.TaskListAdapter;
import com.lori.ui.base.BaseBottomSheetDialog;
import com.lori.ui.base.TimeEntryPartBaseListAdapter;
import com.lori.ui.fragment.DayFragmentStylist;
import com.lori.ui.presenter.TimeEntryEditDialogPresenter;
import com.rey.material.app.BottomSheetDialog;
import nucleus.factory.RequiresPresenter;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;

/**
 * @author artemik
 */
@RequiresPresenter(TimeEntryEditDialogPresenter.class)
public class EditTimeEntryDialog extends BaseBottomSheetDialog<TimeEntryEditDialogPresenter> {

    private static final int DIALOG_ANIMATION_DURATION = 150;

    private static final int RIPPLE_COVER_RADIUS = 500;

    @BindView(R.id.projectsListView)
    RecyclerView projectsListView;

    @BindView(R.id.tasksListView)
    RecyclerView tasksListView;

    @BindView(R.id.activityTypesListView)
    RecyclerView activityTypesListView;

    @BindView(R.id.hoursLinearLayout)
    LinearLayout hoursLinearLayout;

    @BindView(R.id.minutesLinearLayout)
    LinearLayout minutesLinearLayout;

    @BindView(R.id.hoursRippleView4)
    MaterialRippleLayout hoursRippleView4;

    @BindView(R.id.hoursTextView4)
    TextView hoursTextView4;

    @BindView(R.id.minutesTextView0)
    TextView minutesTextView0;

    @BindView(R.id.minutesRippleView0)
    MaterialRippleLayout minutesRippleView0;

    @BindView(R.id.deleteButton)
    Button deleteButton;

    @BindView(R.id.confirmButton)
    Button confirmButton;

    private final Calendar dayDate;

    public EditTimeEntryDialog(Application application, Context context, Calendar dayDate, TimeEntry timeEntryToEdit) {
        super(application, context);

        this.dayDate = dayDate;
        getPresenter().setDayDate(dayDate);
        getPresenter().setTimeEntryToEdit(timeEntryToEdit);

        afterConstruct();
    }

    @Override
    public View createView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.dialog_time_entry_edit, null);
    }

    @Override
    public void onViewCreated(View view) {
        super.onViewCreated(view);

        initEntryPartListView(projectsListView, new ProjectListAdapter(this));
        initEntryPartListView(tasksListView, new TaskListAdapter(this));
        initEntryPartListView(activityTypesListView, new ActivityTypeListAdapter(this));

        int dialogWeekColor = DayFragmentStylist.getBodyBackgroundColor(getContext(), dayDate);
        hoursLinearLayout.setBackgroundColor(dialogWeekColor);
        minutesLinearLayout.setBackgroundColor(dialogWeekColor);

        initHourClicks();
        initMinutesClicks();

        setButtonsAreForEdit(false);
        setButtonsAreActive(false);

        heightParam(ViewGroup.LayoutParams.WRAP_CONTENT);
        inDuration(DIALOG_ANIMATION_DURATION);
        outDuration(DIALOG_ANIMATION_DURATION);
        inInterpolator(new AccelerateDecelerateInterpolator());
        outInterpolator(new AccelerateInterpolator());
        disableSwipeDismiss();
    }

    private void initEntryPartListView(RecyclerView listView, TimeEntryPartBaseListAdapter adapter) {
        int backgroundColor = DayFragmentStylist.getBodyBackgroundColor(getContext(), dayDate);
        listView.setBackgroundColor(backgroundColor);

        listView.setAdapter(adapter);

        listView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Disable buggy animations.
        listView.setItemAnimator(null);
    }

    private void initHourClicks() {
        for (int i = 0; i < hoursLinearLayout.getChildCount(); i++) {
            MaterialRippleLayout ripple = (MaterialRippleLayout) hoursLinearLayout.getChildAt(i);
            TextView hourTextView = ripple.getChildView();
            hourTextView.setOnClickListener(v -> {
                hideRippleForOtherHourViews(hourTextView);
                getPresenter().onHourClick(Integer.parseInt(hourTextView.getText().toString()));
            });
        }
    }

    private void initMinutesClicks() {
        for (int i = 0; i < minutesLinearLayout.getChildCount(); i++) {
            MaterialRippleLayout ripple = (MaterialRippleLayout) minutesLinearLayout.getChildAt(i);
            TextView minuteTextView = ripple.getChildView();
            minuteTextView.setOnClickListener(v -> {
                hideRippleForOtherMinuteViews(minuteTextView);
                getPresenter().onMinutesClick(Integer.parseInt(minuteTextView.getText().toString()));
            });
        }
    }

    private void hideRippleForOtherHourViews(TextView selectedHoursTextView) {
        for (int i = 0; i < hoursLinearLayout.getChildCount(); i++) {
            MaterialRippleLayout ripple = (MaterialRippleLayout) hoursLinearLayout.getChildAt(i);
            TextView hoursTextView = ripple.getChildView();
            if (!selectedHoursTextView.equals(hoursTextView)) {
                ripple.setRadius(0);
            }
        }
    }

    private void hideRippleForOtherMinuteViews(TextView selectedMinutesTextView) {
        for (int i = 0; i < minutesLinearLayout.getChildCount(); i++) {
            MaterialRippleLayout ripple = (MaterialRippleLayout) minutesLinearLayout.getChildAt(i);
            TextView minutesTextView = ripple.getChildView();
            if (!selectedMinutesTextView.equals(minutesTextView)) {
                ripple.setRadius(0);
            }
        }
    }

    public void setAvailableProjects(List<Project> projects) {
        ((ProjectListAdapter) projectsListView.getAdapter()).setItems(projects);

        if (projects != null && !projects.isEmpty()) {
            Project firstProject = projects.get(0);
            setButtonsAreActive(true);
            onProjectClick(firstProject);
        } else {
            setButtonsAreActive(false);
        }
    }

    public void onProjectClick(Project project) {
        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
            ((TaskListAdapter) tasksListView.getAdapter()).setItems(project.getTasks());
            tasksListView.getLayoutManager().scrollToPosition(0);

            onTaskClick(project.getTasks().get(0));
        } else {
            setButtonsAreActive(false);
        }

        if (project.getActivityTypes() != null && !project.getActivityTypes().isEmpty()) {
            ((ActivityTypeListAdapter) activityTypesListView.getAdapter()).setItems(project.getActivityTypes());
            activityTypesListView.getLayoutManager().scrollToPosition(0);

            onActivityTypeClick(project.getActivityTypes().get(0));
        } else {
            setButtonsAreActive(false);
        }

        getPresenter().onProjectClick(project);
    }

    public void onTaskClick(Task task) {
        getPresenter().onTaskClick(task);
    }

    public void onActivityTypeClick(ActivityType activityType) {
        getPresenter().onActivityTypeClick(activityType);
    }

    public void setSelectedProject(Project project) {
        ((ProjectListAdapter) projectsListView.getAdapter()).select(project);
        onProjectClick(project);
    }

    public void setSelectedTask(Task task) {
        ((TaskListAdapter) tasksListView.getAdapter()).select(task);
        onTaskClick(task);
    }

    public void setSelectedActivityType(ActivityType activityType) {
        ((ActivityTypeListAdapter) activityTypesListView.getAdapter()).select(activityType);
        onActivityTypeClick(activityType);
    }

    public void setSelectedHour(int hour) {
        for (int i = 0; i < hoursLinearLayout.getChildCount(); i++) {
            MaterialRippleLayout ripple = (MaterialRippleLayout) hoursLinearLayout.getChildAt(i);
            TextView hourTextView = ripple.getChildView();
            if (hourTextView.getText().equals(String.valueOf(hour))) {
                hourTextView.performClick();
                ripple.setRadius(RIPPLE_COVER_RADIUS);
            }
        }
    }

    public void setSelectedMinute(int minute) {
        for (int i = 0; i < minutesLinearLayout.getChildCount(); i++) {
            MaterialRippleLayout ripple = (MaterialRippleLayout) minutesLinearLayout.getChildAt(i);
            TextView minuteTextView = ripple.getChildView();
            if (minuteTextView.getText().equals(String.valueOf(minute))) {
                minuteTextView.performClick();
                ripple.setRadius(RIPPLE_COVER_RADIUS);
            }
        }
    }

    public void setButtonsAreActive(boolean active) {
        UiUtils.setButtonEnabled(confirmButton, active);
        UiUtils.setButtonEnabled(deleteButton, active);

    }

    public void setButtonsAreForEdit(boolean forEdit) {
        confirmButton.setText(forEdit ?
                getContext().getString(R.string.dialog_time_entry_save_button_text) :
                getContext().getString(R.string.dialog_time_entry_add_button_text));

        deleteButton.setVisibility(forEdit ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.confirmButton)
    public void onConfirmButtonClick(View view) {
        getPresenter().onConfirmButtonClick();
    }

    @OnClick(R.id.deleteButton)
    public void onDeleteButtonClick(View view) {
        getPresenter().onDeleteButtonClick();
    }

    /**
     * Sets very high speed required to dismiss the dialog.
     * This is the only way so far to prevent the dialog from swipe close.
     */
    private void disableSwipeDismiss() {
        Field f = null;
        try {
            f = BottomSheetDialog.class.getDeclaredField("mMinFlingVelocity");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);

        try {
            f.set(this, 100000000);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
