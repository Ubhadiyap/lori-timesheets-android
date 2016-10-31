package com.lori.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lori.R;
import com.lori.ui.base.BaseFragment;
import com.lori.ui.presenter.WeekFragmentPresenter;
import nucleus.factory.RequiresPresenter;

import java.util.Calendar;


/**
 * @author artemik
 */
@RequiresPresenter(WeekFragmentPresenter.class)
public class WeekFragment extends BaseFragment<WeekFragmentPresenter> {

    private static final String MONDAY_DATE_KEY = "MONDAY_DATE_KEY";

    private Calendar mondayDate;

    private boolean menuVisible;

    public WeekFragment() {
    }

    public static WeekFragment newInstance(Calendar mondayDate) {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(MONDAY_DATE_KEY, mondayDate);

        WeekFragment fragment = new WeekFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mondayDate = (Calendar) getArguments().getSerializable(MONDAY_DATE_KEY);
        getPresenter().setMondayDate(mondayDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_week, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentTransaction tx = getChildFragmentManager().beginTransaction();
        tx.add(R.id.fragmentMonday, createDayFragment(0, mondayDate)); // 0 - day of week.
        tx.add(R.id.fragmentTuesday, createDayFragment(1, mondayDate));
        tx.add(R.id.fragmentWednesday, createDayFragment(2, mondayDate));
        tx.add(R.id.fragmentThursday, createDayFragment(3, mondayDate));
        tx.add(R.id.fragmentFriday, createDayFragment(4, mondayDate));
        tx.add(R.id.fragmentSaturday, createDayFragment(5, mondayDate));
        tx.add(R.id.fragmentSunday, createDayFragment(6, mondayDate));
        tx.add(R.id.fragmentWeekTotal, createWeekTotalFragment(mondayDate));
        tx.commit();
    }

    /**
     * Sets if the fragment is visible to the user.
     * This method may be called BEFORE the fragment's onCreate() is invoked, so presenter may be not constructed at the
     * time, therefore:
     * - if that's the case, the value is saved only to be used later from onResume();
     * - otherwise it's called after activity's become resumed, which means onResume() won't be called, so the value is
     * propagated to the presenter at once.
     *
     * Such behavior guarantees the presenter is notified about fragments visibility when the fragment being resumed.
     */
    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        this.menuVisible = menuVisible;

        if (isResumed()) {
            getPresenter().onFragmentVisibilityToUserChanged(this.menuVisible);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().onFragmentVisibilityToUserChanged(menuVisible);
    }

    private DayFragment createDayFragment(int dayOfWeekOffset, Calendar mondayDate) {
        Calendar weekDayDate = (Calendar) mondayDate.clone();
        weekDayDate.add(Calendar.DAY_OF_WEEK, dayOfWeekOffset);
        return DayFragment.newInstance(weekDayDate);
    }

    private WeekTotalFragment createWeekTotalFragment(Calendar mondayDate) {
        return WeekTotalFragment.newInstance(mondayDate);
    }
}
