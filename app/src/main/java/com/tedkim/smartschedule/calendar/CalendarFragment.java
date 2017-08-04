package com.tedkim.smartschedule.calendar;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tedkim.smartschedule.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.listeners.OnExpDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthScrollListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;

/**
 * @author 김태원
 * @file CalendarFragment.java
 * @brief Show schedules with calendar
 * @date 2017.07.31
 */

public class CalendarFragment extends Fragment {

    // calendar components
    TextView mCalendarTitle;
    ExpCalendarView mCalendarView;
    ImageView mCalendarStyle;
    OnCalendarSelectedListener mCallback;
    String mSelectedDate;

    // list components
    RecyclerView mScheduleList;
    RecyclerView.LayoutManager mLayoutManager;

    private boolean isExpanded = true;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Log.d("CHECK_ENTER", "Calendar Fragment -------------------");

        initView(view);

        setRecyclerView();

        setCalendarAction();

        return view;
    }

    private void initView(View view) {

        mCalendarTitle = (TextView) view.findViewById(R.id.textView_monthTitle);
        mCalendarTitle.setText(Calendar.getInstance().get(Calendar.YEAR) + "년 " + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "월");

        mCalendarView = (ExpCalendarView) view.findViewById(R.id.calendarView_expCalendar);
        mCalendarStyle = (ImageView) view.findViewById(R.id.button_calendarStyle);

        mScheduleList = (RecyclerView) view.findViewById(R.id.recyclerView_scheduleList);
    }

    private void setRecyclerView() {

        CalendarScheduleListAdapter adapter = new CalendarScheduleListAdapter(getContext(), getActivity());
        mScheduleList.setAdapter(adapter);

        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);

    }

    private void setCalendarAction() {

        // Expand & Shrink Calendar
        mCalendarStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("CHECK_CALENDAR", ">>>>>>>>>>>>>>" + isExpanded);

                if (isExpanded) {

                    CellConfig.Month2WeekPos = CellConfig.middlePosition;
                    CellConfig.ifMonth = false;

                    mCalendarStyle.setImageResource(R.mipmap.icon_arrow_down);
                    mCalendarView.shrink();
                    isExpanded = false;

                } else {

                    CellConfig.Week2MonthPos = CellConfig.middlePosition;
                    CellConfig.ifMonth = true;

                    mCalendarStyle.setImageResource(R.mipmap.icon_arrow_up);
                    mCalendarView.expand();
                    isExpanded = true;
                }
            }
        });

        // Set calendar component listeners
        mCalendarView.setOnDateClickListener(new OnExpDateClickListener() {

            @Override
            public void onDateClick(View view, DateData date) {
                super.onDateClick(view, date);

                mSelectedDate = String.format("%d-%d-%d", date.getYear(), date.getMonth(), date.getDay());
                try {

                    mCallback.onDateSelectedListener(new SimpleDateFormat("yyyy-M-d").parse(mSelectedDate));

                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("PARSE_ERROR", "Before parsing >>>>>>>>>>>>");
                }

                Toast.makeText(getContext(), mSelectedDate, Toast.LENGTH_SHORT).show();

            }

        }).setOnMonthScrollListener(new OnMonthScrollListener() {

            @Override
            public void onMonthChange(int year, int month) {
                mCalendarTitle.setText(String.format("%d년 %d월", year, month));
            }

            @Override
            public void onMonthScroll(float positionOffset) {
//                Log.i("listener", "onMonthScroll:" + positionOffset);
            }
        });
    }
}

