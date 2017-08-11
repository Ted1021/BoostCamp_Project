package com.tedkim.smartschedule.calendar;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ScheduleData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author 김태원
 * @file CalendarFragment.java
 * @brief Show schedules with calendar
 * @date 2017.07.31
 */

public class CalendarFragment extends Fragment {

    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("yyyy년 M월", Locale.getDefault());

    // calendar components
    OnCalendarSelectedListener mCallback;
    TextView mCalendarTitle;
    CompactCalendarView mCalendarView;
    ImageButton mCalendarStyle;
    Animation mAnimation;
    boolean isExpanded = false;
    int mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
    int mCurrentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;

    // list components
    RecyclerView mScheduleList;
    RecyclerView.LayoutManager mLayoutManager;
    ScheduleListRealmAdapter mAdapter;
    LinearLayout mNoSchedule;

    // Realm instance
    Realm mRealm;

    // Schedule List Dataset
    RealmResults<ScheduleData> mDataset;

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

        mCalendarView = (CompactCalendarView) view.findViewById(R.id.calendarView_expCalendar);
        mCalendarView.displayOtherMonthDays(true);
        mCalendarStyle = (ImageButton) view.findViewById(R.id.button_calendarStyle);

        mNoSchedule = (LinearLayout) view.findViewById(R.id.layout_no_schedule);
        mScheduleList = (RecyclerView) view.findViewById(R.id.recyclerView_scheduleList);
    }

    private void setRecyclerView() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        checkExistSchedules(date);

        mDataset = mRealm.where(ScheduleData.class).equalTo("date", dateFormatForDisplaying.format(date)).findAll();
        Log.e("CHECK_DATA", dateFormatForDisplaying.format(date));
        showNoItemImage();

        mAdapter = new ScheduleListRealmAdapter(mDataset, true, getContext(), getActivity());
        mScheduleList.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);
    }

    private void setCalendarAction() {

        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                updateScheduleList(dateClicked);
                mCallback.onDateSelectedListener(dateFormatForDisplaying.format(dateClicked));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                checkExistSchedules(firstDayOfNewMonth);
                mCalendarTitle.setText(dateFormatForMonth.format(firstDayOfNewMonth));
            }
        });

        // Expand & Shrink Calendar
        mCalendarStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mCalendarView.isAnimating()) {
                    if (isExpanded) {

                        mCalendarStyle.setImageResource(R.mipmap.icon_arrow_up);
                        mCalendarStyle.setAnimation(mAnimation);

                        mCalendarView.clearAnimation();
                        mCalendarView.showCalendar();
                    }
                    else {

                        mCalendarStyle.setImageResource(R.mipmap.icon_arrow_down);
                        mCalendarStyle.startAnimation(mAnimation);

                        mCalendarView.clearAnimation();
                        mCalendarView.hideCalendar();
                    }
                    isExpanded = !isExpanded;
                }
            }
        });
    }

    // 특정 날짜가 선택 되었을 때, mDataset 을 update 시키는 method
    private void updateScheduleList(Date date) {

        Log.d("CHECK_UPDATE", "inside onclick " + dateFormatForDisplaying.format(date));

        // realm query call
        mDataset = mRealm.where(ScheduleData.class).equalTo("date", dateFormatForDisplaying.format(date)).findAll();
        Log.e("CHECK_DATA", ">>>>>>>>>>> "+mDataset.size());
        mAdapter = new ScheduleListRealmAdapter(mDataset, true, getContext(), getActivity());

        mScheduleList.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        mDataset.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ScheduleData>>() {
            @Override
            public void onChange(RealmResults<ScheduleData> collection, OrderedCollectionChangeSet changeSet) {

                mAdapter.notifyDataSetChanged();
                showNoItemImage();
            }
        });
        showNoItemImage();
    }

    // 등록 된 모든 스케줄들에 대해 Dot 마커를 찍는다
    private void checkExistSchedules(Date date) {

        List<Event> eventList = new ArrayList<>();
        RealmResults<ScheduleData> result = mRealm.where(ScheduleData.class).contains("date", dateFormatForDisplaying.format(date)).findAll();
        for (ScheduleData data : result) {
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(data.getDate());
                Log.e("CHECK_MILLIS",">>>>>> "+date.getTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }
            eventList.add(new Event(ContextCompat.getColor(getContext(), R.color.colorActivation), date.getTime()));
        }
        mCalendarView.addEvents(eventList);
    }

    private void showNoItemImage() {

        if (mDataset.size() != 0) {
            mNoSchedule.setVisibility(View.GONE);
        } else {
            mNoSchedule.setVisibility(View.VISIBLE);
        }
    }
}
