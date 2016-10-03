package com.lori.ui.adapter;

import com.lori.core.entity.Project;
import com.lori.ui.base.TimeEntryPartBaseListAdapter;
import com.lori.ui.dialog.EditTimeEntryDialog;

/**
 * @author artemik
 */
public class ProjectListAdapter extends TimeEntryPartBaseListAdapter<Project> {
    public ProjectListAdapter(EditTimeEntryDialog dialog) {
        super(dialog);
    }

    @Override
    protected String toString(Project item) {
        return item.getName();
    }

    @Override
    protected void onItemClick(Project item) {
        dialog.onProjectClick(item);
    }
}
