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

    TabLayout mTabLayout;
    ViewPager mViewPager;
    FloatingActionButton mFloatingButton;

    int mSelectedColor, mUnSelectedColor;
    int mCurrentFragment = FRAG_SCHEDULE;

    Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("CHECK_ENTER", "Home Activity -------------------");

        initView();

        setAction();
    }

    private void initView() {

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFloatingButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        mSelectedColor = ContextCompat.getColor(HomeActivity.this, R.color.colorActivation);
        mUnSelectedColor = ContextCompat.getColor(HomeActivity.this, R.color.colorLightGray);
    }

    private void setAction() {

        mDate = getTime();

        // viewPager action
        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
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

        // tabLayout action
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
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
                                    mDate = getTime();
                                }

                                Log.d("CHECK_DATE", "Before Register >>>>>>>>>>>>>>>" + mDate);

                                intent.putExtra("DATE", mDate);
                                startActivity(intent);
                            }
                        });
            }
        });
    }

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

    private Date getTime() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        return date;
    }

    @Override
    public void onDateSelectedListener(Date date) {

        mDate = date;

        Log.i("CHECK_DATE", "Date from calendar >>>>>>>>>>>" + mDate);
    }

}
