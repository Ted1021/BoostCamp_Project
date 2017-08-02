package com.tedkim.smartschedule.calendar;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import com.tedkim.smartschedule.R;

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
    RecyclerView mScheduleList;

    OnCalendarSelectedListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnCalendarSelectedListener) context;
        } catch (ClassCastException e) {

            e.printStackTrace();
            Log.e("ERROR_DATE", "Error on attaching listener");
        }
    }

    public CalendarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Log.d("CHECK_ENTER","Calendar Fragment -------------------");

        initView(view);

        return view;
    }

    private void initView(View view){

        mCalendarView = (CalendarView) view.findViewById(R.id.calendarView);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

                month = month+1;

                String date = year+"-"+month+"-"+dayOfMonth;
                Log.d("CHECK_DATE","Calendar Fragment >>>>>>>>>>>>>>>>>>>"+date);

                mCallback.onDateSelectedListener(date);
            }
        });


    }

}
