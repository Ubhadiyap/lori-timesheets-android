package com.lori.core.event;

import com.lori.core.entity.TimeEntry;
import org.apache.commons.lang3.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author artemik
 */
public class MultipleDaysUpdateEvent {
    private final List<TimeEntry> allEntries;
    private Calendar startRangeDate;
    private Calendar endRangeDate;

    public MultipleDaysUpdateEvent(List<TimeEntry> allEntries, Calendar startRangeDate, Calendar endRangeDate) {
        this.allEntries = allEntries;
        this.startRangeDate = startRangeDate;
        this.endRangeDate = endRangeDate;
    }

    public List<TimeEntry> getAllTimeEntries(Calendar day) {
        if (!isDayInRange(day)) {
            return null;
        }

        List<TimeEntry> dayAllEntries = new ArrayList<>();

        for (TimeEntry entry : allEntries) {
            if (DateUtils.isSameDay(entry.getDate(), day.getTime())) {
                dayAllEntries.add(entry);
            }
        }

        return dayAllEntries;
    }

    public boolean isDayInRange(Calendar day) {
        return (startRangeDate.before(day) || DateUtils.isSameDay(startRangeDate, day))
                &&
                (day.before(endRangeDate) || DateUtils.isSameDay(endRangeDate, day));


    }
}
