package com.lori.ui.util;

import android.content.Context;
import com.lori.R;

import java.util.Calendar;

/**
 * @author artemik
 */
public class DateHelper {

    private static final int[] MONTH_NAMES = new int[] {
            R.string.month_january_name,
            R.string.month_february_name,
            R.string.month_march_name,
            R.string.month_april_name,
            R.string.month_may_name,
            R.string.month_june_name,
            R.string.month_july_name,
            R.string.month_august_name,
            R.string.month_september_name,
            R.string.month_october_name,
            R.string.month_november_name,
            R.string.month_december_name,
    };

    private DateHelper() {

    }

    public static String getMonthName(Calendar date, Context context) {
        return getMonthName(date.get(Calendar.MONTH), context);
    }

    public static String getMonthName(int month, Context context) {
        // Calendar months start with 0. No need for -1 adjustment.
        return context.getString(MONTH_NAMES[month]);
    }

    public static Calendar getCurrentDate() {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY); // ATTENTION: It doesn't affect DAY_OF_WEEK.
        return currentDate;
    }

    public static int getDayOfWeek(Calendar date) {
        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK) - 1;
        return dayOfWeek == 0 ? 7 : dayOfWeek;
    }
}
