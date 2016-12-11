package com.lori.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.lori.R;
import com.lori.core.entity.TimeEntry;
import com.lori.ui.adapter.TimeEntryListAdapter;
import com.lori.ui.base.BaseFragment;
import com.lori.ui.dialog.EditTimeEntryDialog;
import com.lori.ui.presenter.DayFragmentPresenter;
import nucleus.factory.RequiresPresenter;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author artemik
 */
@RequiresPresenter(DayFragmentPresenter.class)
public class DayFragment extends BaseFragment<DayFragmentPresenter> {

    private static final String DAY_DATE_KEY = "DAY_DATE_KEY";

    EditTimeEntryDialog mDialog;

    @BindView(R.id.totalTimeSpentTextView)
    TextView totalTimeSpentTextView;

    @BindView(R.id.todayImageView)
    ImageView todayImageView;

    @BindView(R.id.headerLayout)
    LinearLayout headerLayout;

    @BindView(R.id.weekAndMonthDayLabel)
    TextView weekAndMonthDayLabel;

    @BindView(R.id.timeEntriesList)
    RecyclerView timeEntriesList;

    @BindView(R.id.addButton)
    Button addButton;

    Calendar dayDate;

    public DayFragment() {
    }

    public static DayFragment newInstance(Calendar dayDate) {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(DAY_DATE_KEY, dayDate);

        DayFragment fragment = new DayFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dayDate = (Calendar) getArguments().getSerializable(DAY_DATE_KEY);
        getPresenter().setDayDate(dayDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewColors(view);

        initWeekDayLabel();

        timeEntriesList.setAdapter(new TimeEntryListAdapter(this, null));
        timeEntriesList.setLayoutManager(new LinearLayoutManager(getContext()));

        if (android.text.format.DateUtils.isToday(dayDate.getTimeInMillis())) {
            todayImageView.setVisibility(View.VISIBLE);
        } else {
            todayImageView.setVisibility(View.GONE);
        }
    }

    public void setTimeEntries(List<TimeEntry> timeEntries) {
        ((TimeEntryListAdapter) timeEntriesList.getAdapter()).setEntries(timeEntries);
    }

    public void addTimeEntry(TimeEntry newTimeEntry) {
        ((TimeEntryListAdapter) timeEntriesList.getAdapter()).addTimeEntry(newTimeEntry);
        timeEntriesList.smoothScrollToPosition(0);
    }

    public void onTimeEntryChanged(TimeEntry changedTimeEntry) {
        ((TimeEntryListAdapter) timeEntriesList.getAdapter()).updateTimeEntry(changedTimeEntry);
    }

    public void onTimeEntryDeleted(TimeEntry deletedTimeEntry) {
        ((TimeEntryListAdapter) timeEntriesList.getAdapter()).deleteTimeEntry(deletedTimeEntry);
    }

    public void setTotalMinutesSpent(int totalMinutesSpent) {
        int totalHoursSpent = totalMinutesSpent / 60;
        int totalMinutesInHourSpent = totalMinutesSpent % 60;
        String totalTimeSpent = String.format(Locale.getDefault(), "%01d:%02d", totalHoursSpent, totalMinutesInHourSpent);
        totalTimeSpentTextView.setText(totalTimeSpent);
    }

    private void initViewColors(View root) {
        int headerBackgroundColor = DayFragmentStylist.getHeaderBackgroundColor(getContext(), dayDate);
        headerLayout.setBackgroundColor(headerBackgroundColor);

        int bodyBackgroundColor = DayFragmentStylist.getBodyBackgroundColor(getContext(), dayDate);
        root.setBackgroundColor(bodyBackgroundColor);
        timeEntriesList.setBackgroundColor(bodyBackgroundColor);

        int buttonBackgroundColor = DayFragmentStylist.getButtonBackgroundColor(getContext(), dayDate);
        addButton.setTextColor(buttonBackgroundColor);
    }

    public void initWeekDayLabel() {
        String weekAndMonthLabelPattern = DayFragmentStylist.getWeekAndMonthDayLabelPattern(getContext(), dayDate);
        int dayOfMonth = dayDate.get(Calendar.DAY_OF_MONTH);
        String formattedText = String.format(weekAndMonthLabelPattern, dayOfMonth);

        weekAndMonthDayLabel.setText(formattedText);
    }

    public void onTimeEntryClick(TimeEntry timeEntry) {
        mDialog = new EditTimeEntryDialog(getActivity().getApplication(), getContext(), dayDate, timeEntry);
        mDialog.show();
    }

    @OnClick(R.id.addButton)
    public void onAddClick() {
        mDialog = new EditTimeEntryDialog(getActivity().getApplication(), getContext(), dayDate, null);
        mDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDialog != null)
            mDialog.dismissImmediately();
        mDialog = null;
    }
}
