package com.tedkim.smartschedule.calendar;


import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ScheduleData;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;
import sun.bob.mcalendarview.CellConfig;
import sun.bob.mcalendarview.MarkStyle;
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
    OnCalendarSelectedListener mCallback;
    TextView mCalendarTitle;
    ExpCalendarView mCalendarView;
    ImageView mCalendarStyle;
    String mSelectedDate;
    Animation mAnimation;
    boolean isExpanded = true;
    int mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
    int mCurrentMonth = Calendar.getInstance().get(Calendar.MONTH)+1;

    // list components
    RecyclerView mScheduleList;
    RecyclerView.LayoutManager mLayoutManager;
    CalendarScheduleListAdapter mAdapter;

    // Realm instance
    Realm mRealm;

    // Schedule List Dataset
    ArrayList<ScheduleData> mDataset = new ArrayList<>();

    Paint p = new Paint();

    public CalendarFragment() {

    }

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

    @Override
    public void onDestroy() {
        super.onDestroy();

        // close Realm database
        mRealm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        Log.d("CHECK_ENTER", "Calendar Fragment -------------------");

        // init Realm database
        mRealm = Realm.getDefaultInstance();

        initView(view);
        setRecyclerView();
        setCalendarAction();

        return view;
    }

    private void initView(View view) {

        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.drop_down);

        mCalendarTitle = (TextView) view.findViewById(R.id.textView_monthTitle);
        mCalendarTitle.setText(String.format("%d년 %d월", mCurrentYear, mCurrentMonth));

        mCalendarView = (ExpCalendarView) view.findViewById(R.id.calendarView_expCalendar);
        mCalendarStyle = (ImageView) view.findViewById(R.id.button_calendarStyle);

        mScheduleList = (RecyclerView) view.findViewById(R.id.recyclerView_scheduleList);

        checkExistSchedules(mCurrentYear, mCurrentMonth);
    }

    private void setRecyclerView() {

        mAdapter = new CalendarScheduleListAdapter(getContext(), getActivity(), mDataset);
        mScheduleList.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);
        mAdapter.notifyDataSetChanged();
    }

    private void setCalendarAction() {

        // Change year & month info on scrolling
        mCalendarView.setOnMonthScrollListener(new OnMonthScrollListener() {
            @Override
            public void onMonthChange(int year, int month) {

                mCalendarTitle.setText(String.format("%d년 %d월", year, month));
                checkExistSchedules(year, month);
            }

            @Override
            public void onMonthScroll(float positionOffset) {
                Log.i("listener", "onMonthScroll:" + positionOffset);
            }
        });

        // Expand & Shrink Calendar
        mCalendarStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeCalendarType();
            }
        });

        // Update schedules when date is clicked
        mCalendarView.setOnDateClickListener(new OnExpDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                super.onDateClick(view, date);

                updateScheduleList(date);
            }
        });
    }

    private void changeCalendarType() {

        Log.d("CHECK_CALENDAR", ">>>>>>>>>>>>>> " + isExpanded);

        if (isExpanded) {

            CellConfig.Month2WeekPos = CellConfig.middlePosition;
            CellConfig.ifMonth = false;

            mCalendarStyle.setImageResource(R.mipmap.icon_arrow_down);
            mCalendarStyle.startAnimation(mAnimation);

            mCalendarView.shrink();
            mCalendarView.startAnimation(mAnimation);

        } else {

            CellConfig.Week2MonthPos = CellConfig.middlePosition;
            CellConfig.ifMonth = true;

            mCalendarStyle.setImageResource(R.mipmap.icon_arrow_up);
            mCalendarStyle.setAnimation(mAnimation);

            mCalendarView.expand();
            mCalendarView.startAnimation(mAnimation);
        }
        isExpanded = !isExpanded;
    }

    // 특정 날짜가 선택 되었을 때, mDataset 을 update 시키는 method
    private void updateScheduleList(DateData date) {

        // convert 'DateData' to 'Date'
        mSelectedDate = String.format("%d-%d-%d", date.getYear(), date.getMonth(), date.getDay());
        mCallback.onDateSelectedListener(mSelectedDate);

        Log.d("CHECK_SELECTION", "=-=-=-=-=-=-=-=-=" + mSelectedDate);
        Toast.makeText(getContext(), mSelectedDate, Toast.LENGTH_SHORT).show();

        // realm query call
        RealmResults<ScheduleData> result = mRealm.where(ScheduleData.class).equalTo("date", mSelectedDate).findAll();
        mDataset.clear();
        mDataset.addAll(mRealm.copyFromRealm(result));
        mAdapter.notifyDataSetChanged();
    }

    // 등록 된 모든 스케줄들에 대해 Dot 마커를 찍는다
    private void checkExistSchedules(int year, int month){

        RealmResults<ScheduleData> result = mRealm.where(ScheduleData.class).contains("date", year+"-"+month).findAll();

        for(ScheduleData data : result){

            int y = Integer.parseInt(data.getDate().split("-")[0]);
            int m = Integer.parseInt(data.getDate().split("-")[1]);
            int d = Integer.parseInt(data.getDate().split("-")[2]);

            mCalendarView.markDate(new DateData(y,m,d).setMarkStyle(new MarkStyle(MarkStyle.DOT, R.color.colorAppTheme)));

        }
    }
}
