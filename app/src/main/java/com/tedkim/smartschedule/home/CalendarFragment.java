package com.tedkim.smartschedule.home;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.tedkim.smartschedule.R;

import java.util.Calendar;

/**
 * @file CalendarFragment.java
 * @brief Show schedules with calendar
 *
 * @author 김태원
 * @date 2017.07.31
 *
 */

public class CalendarFragment extends Fragment {

    CalendarView mCalendarView;
    Calendar mCurrentDate;

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        initView(view);

        return view;
    }

    private void initView(View view){

        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                mCurrentDate.getInstance().set(year, month, dayOfMonth);
            }
        });
    }

}
