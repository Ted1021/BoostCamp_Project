package com.tedkim.smartschedule.home;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.calendar.CalendarFragment;
import com.tedkim.smartschedule.calendar.OnCalendarSelectedListener;
import com.tedkim.smartschedule.regist.RegistActivity;
import com.tedkim.smartschedule.schedule.ScheduleFragment;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.DateConvertUtil;

import java.util.Date;

import top.wefor.circularanim.CircularAnim;

import static android.support.v4.view.ViewPager.SCROLL_STATE_DRAGGING;
import static android.support.v4.view.ViewPager.SCROLL_STATE_IDLE;

/**
 * @author 김태원
 * @file HomeActivity.java
 * @brief Set Schedule Fragment and Calendar Fragment with TabLayout & ViewPager
 * @date 2017.07.31
 */

public class HomeActivity extends AppCompatActivity implements OnCalendarSelectedListener {

    private final int MAX_FRAGMENT = 2;

    private final int FRAG_SCHEDULE = 0;
    private final int FRAG_CALENDAR = 1;

    public static final long ACTION_CREATE = -1;

    TabLayout mTabLayout;
    ViewPager mViewPager;
    FloatingActionButton mFloatingButton;

    int mSelectedColor, mUnSelectedColor;
    int mCurrentFragment = FRAG_SCHEDULE;

    String mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("CHECK_ENTER", "Home Activity -------------------");

        initView();

        setAction();
    }

    private void initView() {

        mSelectedColor = ContextCompat.getColor(HomeActivity.this, R.color.colorActivation);
        mUnSelectedColor = ContextCompat.getColor(HomeActivity.this, R.color.colorLightGray);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.getTabAt(0).getIcon().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_IN);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFloatingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
    }

    private void setAction() {

        // init current time
        getTime();

        // viewPager actions
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == FRAG_CALENDAR) {
                    mCurrentFragment = FRAG_CALENDAR;
                } else {
                    mCurrentFragment = FRAG_SCHEDULE;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state) {

                    case SCROLL_STATE_IDLE:
                        mFloatingButton.show();
                        break;

                    case SCROLL_STATE_DRAGGING:
                        mFloatingButton.hide();
                        break;
                }
            }
        });

        // tabLayout actions
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mFloatingButton.hide();
                tab.getIcon().setColorFilter(mUnSelectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // floating button action
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CircularAnim.fullActivity(HomeActivity.this, v)
                        .colorOrImageRes(R.color.colorAppTheme)
                        .go(new CircularAnim.OnAnimationEndListener() {
                            @Override
                            public void onAnimationEnd() {

                                Intent intent = new Intent(HomeActivity.this, RegistActivity.class);

                                if (mCurrentFragment == FRAG_SCHEDULE) {
                                    getTime();
                                }

                                Log.d("CHECK_DATE", "Before Register >>>>>>>>>>>>>>>" + mDate);

                                intent.putExtra("DATE", mDate);
                                intent.putExtra("ID", "");
                                startActivityForResult(intent, AppController.REQ_CREATE);
                            }
                        });
            }
        });

        // Enable interaction between ViewPager and TabLayout
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    // Fragment Adapter for ViewPager
    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case FRAG_SCHEDULE:
                    return new ScheduleFragment();

                case FRAG_CALENDAR:
                    return new CalendarFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return MAX_FRAGMENT;
        }
    }

    // Get current time
    private void getTime() {

        // get current day to Date Type
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        mDate = DateConvertUtil.date2string(date);
    }

    // Add Listener for choosing date from Calendar Fragment
    @Override
    public void onDateSelectedListener(Date date) {

        mDate = DateConvertUtil.date2string(date);
        Log.i("CHECK_DATE", "Date from calendar >>>>>>>>>>>" + mDate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AppController.REQ_CREATE){

        }
    }
}
