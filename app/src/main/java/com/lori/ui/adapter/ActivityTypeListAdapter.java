package com.lori.ui.adapter;

import com.lori.core.entity.ActivityType;
import com.lori.ui.base.TimeEntryPartBaseListAdapter;
import com.lori.ui.dialog.EditTimeEntryDialog;

/**
 * @author artemik
 */
public class ActivityTypeListAdapter extends TimeEntryPartBaseListAdapter<ActivityType> {
    public ActivityTypeListAdapter(EditTimeEntryDialog dialog) {
        super(dialog);
    }

    @Override
    protected String toString(ActivityType item) {
        return item.getName();
    }

    @Override
    protected void onItemClick(ActivityType item) {
        dialog.onActivityTypeClick(item);
    }
}
