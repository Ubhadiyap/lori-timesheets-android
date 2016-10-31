package com.lori.ui.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.BindView;
import com.lori.R;
import com.lori.ui.adapter.WeekPagerAdapter;
import com.lori.ui.base.BaseActivity;

/**
 * @author artemik
 */
public class WeekActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.weekPager)
    ViewPager weekPager;

    @Override
    protected Integer getContentViewId() {
        return R.layout.activity_week;
    }

    @Override
    protected void onContentViewSet() {
        inject();

        setSupportActionBar(toolbar);

        WeekPagerAdapter adapter = new WeekPagerAdapter(getSupportFragmentManager());
        weekPager.setAdapter(adapter);
        weekPager.setCurrentItem(adapter.getMiddlePosition());
        weekPager.setOffscreenPageLimit(10);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_week_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_go_to_today:
                weekPager.setCurrentItem(((WeekPagerAdapter) weekPager.getAdapter()).getMiddlePosition());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
