package com.tedkim.smartschedule.Home;

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
import android.view.View;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.Regist.RegistActivity;

/**
 * @author 김태원
 * @file HomeActivity.java
 * @brief Set Schedule Fragment and Calendar Fragment with TabLayout & ViewPager
 * @date 2017.07.31
 */

public class HomeActivity extends AppCompatActivity {

    private final int MAX_FRAGMENT = 2;

    private final int TAB_SCHEDULE = 0;
    private final int TAB_CALENDAR = 1;

    TabLayout mTabLayout;
    ViewPager mViewPager;
    FloatingActionButton mFloatingButton;

    int mSelectedColor, mUnSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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

        mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(mSelectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(mUnSelectedColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO - 현재날짜 또는 선택 된 날짜 정보를 함께 전송 해야 함. 아마도 Fragment 마다 달아줘야 할듯
                Intent intent = new Intent(HomeActivity.this, RegistActivity.class);
                startActivity(intent);

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

                case TAB_SCHEDULE:

                    return new ScheduleFragment();

                case TAB_CALENDAR:

                    return new CalendarFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return MAX_FRAGMENT;
        }
    }
}
