package com.lori.ui.presenter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.lori.core.event.MultipleDaysUpdateEvent;
import com.lori.core.service.TimeEntryService;
import com.lori.ui.base.BasePresenter;
import com.lori.ui.fragment.WeekFragment;
import com.lori.ui.util.DateHelper;

import javax.inject.Inject;
import java.util.Calendar;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author artemik
 */
public class WeekFragmentPresenter extends BasePresenter<WeekFragment> {
    private static final String TAG = WeekFragmentPresenter.class.getSimpleName();

    private static final int LOAD_WEEK_TIME_ENTRIES = 0;
    private static final int NUMBER_OF_DAYS_IN_WEEK = 7;

    @Inject
    TimeEntryService timeEntryService;

    private Calendar mondayDate;


    public void setMondayDate(Calendar mondayDate) {
        this.mondayDate = mondayDate;
        start(LOAD_WEEK_TIME_ENTRIES);
    }

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        restartableReplay(LOAD_WEEK_TIME_ENTRIES,
                () -> timeEntryService.loadTimeEntries(mondayDate)
                        .observeOn(mainThread()),
                (weekFragment, timeEntries) -> {
                    final Calendar sundayDate = ((Calendar) mondayDate.clone());
                    sundayDate.add(Calendar.DAY_OF_MONTH, NUMBER_OF_DAYS_IN_WEEK - 1);

                    eventBus.post(new MultipleDaysUpdateEvent(timeEntries, mondayDate, sundayDate));
                },
                (weekFragment, throwable) -> Log.e(TAG, "Failed to load week time entries.", throwable));
    }

    public void onMenuVisibilitySet(boolean visible) {
        if (!visible) {
            return;
        }

        add(view()
                .filter(weekFragment -> weekFragment != null)
                .take(1)
                .subscribe(weekFragment -> {
                    String monthName = DateHelper.getMonthName(mondayDate, weekFragment.getContext());
                    weekFragment.getActivity().setTitle(monthName);

                    int year = mondayDate.get(Calendar.YEAR);
                    ((AppCompatActivity) weekFragment.getActivity()).getSupportActionBar().setSubtitle(String.valueOf(year));
                })
        );
    }
}
