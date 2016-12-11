package com.lori.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import com.lori.ui.fragment.WeekFragment;
import com.lori.ui.util.DateHelper;

import java.util.Calendar;

/**
 * @author artemik
 */
public class WeekPagerAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT = 1000; //TODO: unlimited fragment loading
    private static final int MIDDLE_POSITION = COUNT / 2;

    public WeekPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    public int getMiddlePosition() {
        return MIDDLE_POSITION;
    }

    @Override
    public int getCount() {
        return COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Calendar mondayDate = getMondayDate(position);
        return WeekFragment.newInstance(mondayDate);
    }

    private Calendar getMondayDate(int position) {
        Calendar currentDate = DateHelper.getCurrentDate();
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        int weekOffset = position - MIDDLE_POSITION;
        currentDate.add(Calendar.WEEK_OF_MONTH, weekOffset);

        return currentDate;
    }
}