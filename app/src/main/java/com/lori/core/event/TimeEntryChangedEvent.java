package com.lori.core.event;

import com.lori.core.entity.TimeEntry;

/**
 * @author artemik
 */
public class TimeEntryChangedEvent {
    public enum EventType {ADDED, CHANGED, REMOVED}

    private EventType eventType;
    private TimeEntry timeEntry;
    private TimeEntry previousTimeEntry;

    public static TimeEntryChangedEvent added(TimeEntry newTimeEntry) {
        TimeEntryChangedEvent event = new TimeEntryChangedEvent();
        event.timeEntry = newTimeEntry;
        event.eventType = EventType.ADDED;
        return event;
    }

    public static TimeEntryChangedEvent removed(TimeEntry removedTimeEntry) {
        TimeEntryChangedEvent event = new TimeEntryChangedEvent();
        event.timeEntry = removedTimeEntry;
        event.eventType = EventType.REMOVED;
        return event;
    }

    public static TimeEntryChangedEvent changed(TimeEntry newTimeEntry, TimeEntry previousTimeEntry) {
        TimeEntryChangedEvent event = new TimeEntryChangedEvent();
        event.timeEntry = newTimeEntry;
        event.previousTimeEntry = previousTimeEntry;
        event.eventType = EventType.CHANGED;
        return event;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public TimeEntry getTimeEntry() {
        return timeEntry;
    }

    public void setTimeEntry(TimeEntry timeEntry) {
        this.timeEntry = timeEntry;
    }

    public TimeEntry getPreviousTimeEntry() {
        return previousTimeEntry;
    }

    public void setPreviousTimeEntry(TimeEntry previousTimeEntry) {
        this.previousTimeEntry = previousTimeEntry;
    }
}
