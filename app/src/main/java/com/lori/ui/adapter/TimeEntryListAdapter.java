package com.lori.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.balysv.materialripple.MaterialRippleLayout;
import com.lori.R;
import com.lori.core.entity.TimeEntry;
import com.lori.ui.fragment.DayFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author artemik
 */
public class TimeEntryListAdapter extends RecyclerView.Adapter<TimeEntryListAdapter.BaseViewHolder> {

    private final List<TimeEntry> entries = new ArrayList<>();
    protected DayFragment fragment;
    private boolean isEmpty;

    public TimeEntryListAdapter(DayFragment fragment, List<TimeEntry> entries) {
        this.fragment = fragment;
        doSetEntries(entries);
    }

    public void setEntries(List<TimeEntry> newEntries) {
        if (newEntries == null) {
            return;
        }

        doSetEntries(newEntries);

        notifyDataSetChanged();
    }

    private void doSetEntries(List<TimeEntry> newEntries) {
        if (newEntries == null) {
            return;
        }

        entries.clear();

        if (newEntries.isEmpty()) {
            isEmpty = true;
        } else {
            isEmpty = false;
            entries.addAll(newEntries);
        }
    }

    public void addTimeEntry(TimeEntry newTimeEntry) {
        entries.add(0, newTimeEntry);
        if (isEmpty) {
            isEmpty = false;
            notifyItemChanged(0); // Due to empty place holder.
        } else {
            notifyItemInserted(0);
        }
    }

    public void updateTimeEntry(TimeEntry updatedTimeEntry) {
        for (int i = 0; i < entries.size(); i++) {
            TimeEntry timeEntry = entries.get(i);
            if (timeEntry.equals(updatedTimeEntry)) {
                entries.set(i, updatedTimeEntry);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void deleteTimeEntry(TimeEntry deletedTimeEntry) {
        for (int i = 0; i < entries.size(); i++) {
            TimeEntry timeEntry = entries.get(i);
            if (timeEntry.getId().equals(deletedTimeEntry.getId())) {
                entries.remove(i);
                isEmpty = entries.isEmpty();
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (isEmpty) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_time_entry_list_placeholder, viewGroup, false);
            return new EmptyListViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.time_entry, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder vh, int i) {
        if (isEmpty) {
            return;
        }

        ViewHolder viewHolder = (ViewHolder) vh;

        TimeEntry entry = entries.get(i);

        Integer minutesSpent = entry.getTimeInMinutes();
        int hoursSpent = minutesSpent / 60;
        int minutesInHourSpent = minutesSpent % 60;

        String timeSpent = String.format(Locale.getDefault(), "%01d:%02d", hoursSpent, minutesInHourSpent);
        viewHolder.timeTextView.setText(timeSpent);

        viewHolder.taskTextView.setText(entry.getTask().getName());

        String projectAndActivityText = String.format("%s (%s)", entry.getTask().getProject().getName(), entry.getActivityType().getName());
        viewHolder.projectAndActivityTextView.setText(projectAndActivityText);

        viewHolder.rippleView.setOnClickListener(v -> fragment.onTimeEntryClick(entry));
    }

    @Override
    public int getItemViewType(int position) {
        return isEmpty ? R.layout.empty_time_entry_list_placeholder : R.layout.time_entry;
    }

    @Override
    public int getItemCount() {
        // 1 for empty placeholder.
        return isEmpty ? 1 : entries.size();
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.rippleView)
        MaterialRippleLayout rippleView;

        @BindView(R.id.timeTextView)
        TextView timeTextView;

        @BindView(R.id.taskTextView)
        TextView taskTextView;

        @BindView(R.id.projectAndActivityTextView)
        TextView projectAndActivityTextView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static class EmptyListViewHolder extends BaseViewHolder {
        public EmptyListViewHolder(View view) {
            super(view);
        }
    }
}
