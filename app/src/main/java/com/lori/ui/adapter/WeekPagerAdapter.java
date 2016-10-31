package com.lori.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.lori.ui.fragment.WeekFragment;
import com.lori.ui.util.DateHelper;

import java.util.Calendar;

/**
 * @author artemik
 */
public class WeekPagerAdapter extends FragmentPagerAdapter {

    private final int count = 3; //TODO: unlimited fragment loading
    private final int middlePosition = count / 2;

    public WeekPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public int getMiddlePosition() {
        return middlePosition;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Fragment getItem(int position) {
        Calendar mondayDate = getMondayDate(position);
        return WeekFragment.newInstance(mondayDate);
    }

    private Calendar getMondayDate(int position) {
        Calendar currentDate = DateHelper.getCurrentDate();
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        int weekOffset = position - middlePosition;
        currentDate.add(Calendar.WEEK_OF_MONTH, weekOffset);

        return currentDate;
    }
}