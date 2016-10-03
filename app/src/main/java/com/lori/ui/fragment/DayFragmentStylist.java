package com.lori.ui.fragment;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import com.lori.R;
import com.lori.ui.util.DateHelper;

import java.util.Calendar;

/**
 * @author artemik
 */
public class DayFragmentStylist {

    private static final int[] WEEK_AND_MONTH_DAY_LABEL_PATTERNS = new int[] {
            R.string.mondayPattern,
            R.string.tuesdayPattern,
            R.string.wednesdayPattern,
            R.string.thursdayPattern,
            R.string.fridayPattern,
            R.string.saturdayPattern,
            R.string.sundayPattern
    };

    private static final int[] HEADER_BACKGROUND_COLORS = new int[] {
            R.color.dark_yellow,
            R.color.dark_orange,
            R.color.dark_deep_orange,
            R.color.dark_deep_brown,
            R.color.dark_purple,
            R.color.dark_deep_purple,
            R.color.dark_deep_grey
    };

    private static final int[] BODY_BACKGROUND_COLORS = new int[] {
            R.color.light_yellow,
            R.color.light_orange,
            R.color.light_deep_orange,
            R.color.light_deep_brown,
            R.color.light_purple,
            R.color.light_deep_purple,
            R.color.light_deep_grey
    };

    private static final int[] BUTTON_BACKGROUND_COLORS = new int[] {
            R.color.darker_yellow,
            R.color.darker_orange,
            R.color.darker_deep_orange,
            R.color.darker_deep_brown,
            R.color.darker_purple,
            R.color.darker_deep_purple,
            R.color.darker_deep_grey
    };

    public static String getWeekAndMonthDayLabelPattern(Context context, Calendar date) {
        return getWeekAndMonthDayLabelPattern(context, DateHelper.getDayOfWeek(date));
    }

    public static int getHeaderBackgroundColor(Context context, Calendar date) {
        return getHeaderBackgroundColor(context, DateHelper.getDayOfWeek(date));
    }

    public static int getBodyBackgroundColor(Context context, Calendar date) {
        return getBodyBackgroundColor(context, DateHelper.getDayOfWeek(date));
    }

    public static int getButtonBackgroundColor(Context context, Calendar date) {
        return getButtonBackgroundColor(context, DateHelper.getDayOfWeek(date));
    }

    public static String getWeekAndMonthDayLabelPattern(Context context, int dayOfWeek) {
        int resourceId = WEEK_AND_MONTH_DAY_LABEL_PATTERNS[dayOfWeek - 1];
        return context.getString(resourceId);
    }

    public static int getHeaderBackgroundColor(Context context, int dayOfWeek) {
        int resourceId = HEADER_BACKGROUND_COLORS[dayOfWeek - 1];
        return ContextCompat.getColor(context, resourceId);
    }

    public static int getBodyBackgroundColor(Context context, int dayOfWeek) {
        int resourceId = BODY_BACKGROUND_COLORS[dayOfWeek - 1];
        return ContextCompat.getColor(context, resourceId);
    }

    public static int getButtonBackgroundColor(Context context, int dayOfWeek) {
        int resourceId = BUTTON_BACKGROUND_COLORS[dayOfWeek - 1];
        return ContextCompat.getColor(context, resourceId);
    }
}
