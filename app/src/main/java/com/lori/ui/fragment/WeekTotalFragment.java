package com.lori.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import com.lori.R;
import com.lori.ui.base.BaseFragment;
import com.lori.ui.presenter.WeekTotalFragmentPresenter;
import nucleus.factory.RequiresPresenter;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author artemik
 */
@RequiresPresenter(WeekTotalFragmentPresenter.class)
public class WeekTotalFragment extends BaseFragment<WeekTotalFragmentPresenter> {

    private static final String MONDAY_DATE_KEY = "MONDAY_DATE_KEY";

    @BindView(R.id.totalWeekTimeSpentTextView)
    TextView totalWeekTimeSpentTextView;

    public WeekTotalFragment() {
    }

    public static WeekTotalFragment newInstance(Calendar mondayDate) {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(MONDAY_DATE_KEY, mondayDate);

        WeekTotalFragment fragment = new WeekTotalFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar mondayDate = (Calendar) getArguments().getSerializable(MONDAY_DATE_KEY);
        getPresenter().setMondayDate(mondayDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_week_total, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setTotalWeekMinutesSpent(int totalWeekMinutesSpent) {
        int totalWeekHoursSpent = totalWeekMinutesSpent / 60;
        int totalWeekMinutesInHourSpent = totalWeekMinutesSpent % 60;
        String totalWeekTimeSpent = String.format(Locale.getDefault(), "%02d:%02d", totalWeekHoursSpent, totalWeekMinutesInHourSpent);

        totalWeekTimeSpentTextView.setText(totalWeekTimeSpent);
    }
}
