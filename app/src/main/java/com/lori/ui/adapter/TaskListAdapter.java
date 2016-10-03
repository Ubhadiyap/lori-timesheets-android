package com.lori.ui.adapter;

import com.lori.core.entity.Task;
import com.lori.ui.base.TimeEntryPartBaseListAdapter;
import com.lori.ui.dialog.EditTimeEntryDialog;

/**
 * @author artemik
 */
public class TaskListAdapter extends TimeEntryPartBaseListAdapter<Task> {
    public TaskListAdapter(EditTimeEntryDialog dialog) {
        super(dialog);
    }

    @Override
    protected String toString(Task item) {
        return item.getName();
    }

    @Override
    protected void onItemClick(Task item) {
        dialog.onTaskClick(item);
    }
}
