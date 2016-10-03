package com.lori.ui.presenter;

import android.os.Bundle;
import com.lori.core.entity.TimeEntry;
import com.lori.core.event.MultipleDaysUpdateEvent;
import com.lori.core.event.TimeEntryChangedEvent;
import com.lori.ui.base.BasePresenter;
import com.lori.ui.fragment.DayFragment;
import org.apache.commons.lang3.time.DateUtils;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.List;

/**
 * @author artemik
 */
public class DayFragmentPresenter extends BasePresenter<DayFragment> {
    private static final String TAG = DayFragmentPresenter.class.getSimpleName();

    private Calendar dayDate;
    int totalMinutesSpent;

    public void setDayDate(Calendar dayDate) {
        this.dayDate = dayDate;
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        eventBus.register(this);
    }

    @Subscribe
    public void onMultipleDaysUpdate(MultipleDaysUpdateEvent event) {
        List<TimeEntry> allDayEntries = event.getAllTimeEntries(dayDate);

        if (allDayEntries != null) {
            totalMinutesSpent = 0;
            for (TimeEntry entry : allDayEntries) {
                totalMinutesSpent += entry.getMinutesSpent();
            }

            view().filter(fragment -> fragment != null)
                    .take(1)
                    .subscribe(fragment -> {
                        fragment.setTimeEntries(allDayEntries);
                        fragment.setTotalMinutesSpent(totalMinutesSpent);
                    });
        }
    }

    @Subscribe
    public void onTimeEntryChanged(TimeEntryChangedEvent event) {
        TimeEntry timeEntry = event.getTimeEntry();

        if (!DateUtils.isSameDay(timeEntry.getDate(), dayDate.getTime())) {
            return;
        }

        switch (event.getEventType()) {
            case ADDED:
                totalMinutesSpent += timeEntry.getMinutesSpent();
                add(view().filter(fragment -> fragment != null)
                        .take(1)
                        .subscribe(fragment -> {
                            fragment.addTimeEntry(timeEntry);
                            fragment.setTotalMinutesSpent(totalMinutesSpent);
                        })
                );
                break;
            case CHANGED:
                totalMinutesSpent -= event.getPreviousTimeEntry().getMinutesSpent();
                totalMinutesSpent += timeEntry.getMinutesSpent();
                add(view().filter(fragment -> fragment != null)
                        .take(1)
                        .subscribe(fragment -> {
                            fragment.onTimeEntryChanged(timeEntry);
                            fragment.setTotalMinutesSpent(totalMinutesSpent);
                        })
                );
                break;
            case REMOVED:
                totalMinutesSpent -= timeEntry.getMinutesSpent();
                add(view().filter(fragment -> fragment != null)
                        .take(1)
                        .subscribe(fragment -> {
                            fragment.onTimeEntryDeleted(timeEntry);
                            fragment.setTotalMinutesSpent(totalMinutesSpent);
                        })
                );
                break;
        }
    }
}
