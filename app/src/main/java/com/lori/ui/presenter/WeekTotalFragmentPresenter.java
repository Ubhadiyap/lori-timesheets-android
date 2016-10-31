package com.lori.ui.presenter;

import android.os.Bundle;
import com.lori.core.entity.TimeEntry;
import com.lori.core.event.MultipleDaysUpdateEvent;
import com.lori.core.event.TimeEntryChangedEvent;
import com.lori.ui.base.BasePresenter;
import com.lori.ui.fragment.WeekTotalFragment;
import org.apache.commons.lang3.time.DateUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.List;

/**
 * @author artemik
 */
public class WeekTotalFragmentPresenter extends BasePresenter<WeekTotalFragment> {
    private static final String TAG = WeekTotalFragmentPresenter.class.getSimpleName();

    private Calendar mondayDate;
    private int totalWeekMinutesSpent;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        eventBus.register(this);
    }

    @Subscribe
    public void onMultipleDaysUpdate(MultipleDaysUpdateEvent event) {
        if (!event.isDayInRange(mondayDate)) {
            return;
        }

        // If monday is present, it's assumed the whole week is present.

        totalWeekMinutesSpent = 0;
        for (int dayOfMonthOffset = 0; dayOfMonthOffset < 7; dayOfMonthOffset++) {
            Calendar nextDay = (Calendar) mondayDate.clone();
            nextDay.add(Calendar.DAY_OF_MONTH, dayOfMonthOffset);

            totalWeekMinutesSpent += collectTotalDayMinutesSpent(nextDay, event);
        }

        updateUiTotalWeekMinutesSpent();
    }

    @Subscribe
    public void onTimeEntryChanged(TimeEntryChangedEvent event) {
        TimeEntry timeEntry = event.getTimeEntry();
        if (!isInThisWeekRange(timeEntry)) {
            return;
        }

        switch (event.getEventType()) {
            case ADDED:
                totalWeekMinutesSpent += timeEntry.getTimeInMinutes();
                break;
            case CHANGED:
                totalWeekMinutesSpent -= event.getPreviousTimeEntry().getTimeInMinutes();
                totalWeekMinutesSpent += timeEntry.getTimeInMinutes();
                break;
            case REMOVED:
                totalWeekMinutesSpent -= timeEntry.getTimeInMinutes();
                break;
        }
        updateUiTotalWeekMinutesSpent();
    }

    private void updateUiTotalWeekMinutesSpent() {
        view().filter(fragment -> fragment != null)
                .take(1)
                .subscribe(fragment -> {
                    fragment.setTotalWeekMinutesSpent(totalWeekMinutesSpent);
                });
    }

    private int collectTotalDayMinutesSpent(Calendar dayDate, MultipleDaysUpdateEvent event) {
        int totalDayMinutesSpent = 0;

        List<TimeEntry> allDayEntries = event.getAllTimeEntries(dayDate);
        if (allDayEntries != null) {
            for (TimeEntry entry : allDayEntries) {
                totalDayMinutesSpent += entry.getTimeInMinutes();
            }
        }

        return totalDayMinutesSpent;
    }

    public boolean isInThisWeekRange(TimeEntry timeEntry) {
        for (int dayOfMonthOffset = 0; dayOfMonthOffset < 7; dayOfMonthOffset++) {
            Calendar nextDay = (Calendar) mondayDate.clone();
            nextDay.add(Calendar.DAY_OF_MONTH, dayOfMonthOffset);

            if (DateUtils.isSameDay(timeEntry.getDate(), nextDay.getTime())) {
                return true;
            }
        }
        return false;
    }

    public void setMondayDate(Calendar mondayDate) {
        this.mondayDate = mondayDate;
    }
}
