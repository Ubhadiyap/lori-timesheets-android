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

    private boolean isMenuVisible;

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
    public void onResume() {
        super.onResume();
        getPresenter().onMenuVisibilitySet(isMenuVisible);
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        isMenuVisible = menuVisible;

        // Workaround to update toolbar title.
        // The values is saved and either will be updated later during resume,
        // or here straightaway.
        if (isResumed()) {
            getPresenter().onMenuVisibilitySet(isMenuVisible);
        }
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
        tx.add(R.id.fragmentMonday, createDayFragment(0, mondayDate));
        tx.add(R.id.fragmentTuesday, createDayFragment(1, mondayDate));
        tx.add(R.id.fragmentWednesday, createDayFragment(2, mondayDate));
        tx.add(R.id.fragmentThursday, createDayFragment(3, mondayDate));
        tx.add(R.id.fragmentFriday, createDayFragment(4, mondayDate));
        tx.add(R.id.fragmentSaturday, createDayFragment(5, mondayDate));
        tx.add(R.id.fragmentSunday, createDayFragment(6, mondayDate));
        tx.add(R.id.fragmentWeekTotal, createWeekTotalFragment(mondayDate));
        tx.commit();
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
