package com.tedkim.smartschedule.schedule;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.RouteData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.CurrentLocation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author 김태원
 * @file ScheduleFragment.java
 * @brief Show today & tomorrow schedules with departure time
 * @date 2017.07.31
 */

public class ScheduleFragment extends Fragment {

    SwipeRefreshLayout mRefreshLayout;

    Button mRoute;

    Realm mRealm;

    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
    RealmResults<ScheduleData> mDataset;
    ScheduleRouteListAdapter mAdapter;
    RecyclerView mScheduleList;
    RecyclerView.LayoutManager mLayoutManager;

    public ScheduleFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("CHECK_ENTER", "Schedule Fragment -------------------");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        // init Realm database
        mRealm = Realm.getDefaultInstance();

        initView(view);
        setRecyclerView();
        setAction();

        return view;
    }

    private void initView(View view) {

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);

        mRoute = (Button) view.findViewById(R.id.button_route);
        mScheduleList = (RecyclerView) view.findViewById(R.id.recyclerView_scheduleList);
    }

    private void setRecyclerView() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        mDataset = mRealm.where(ScheduleData.class).equalTo("date", dateFormatForDisplaying.format(date)).findAll();

        mAdapter = new ScheduleRouteListAdapter(mDataset, true, getContext());
        mScheduleList.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);
    }

    private void setAction() {

        // 모든 오늘자 스케줄에 대한 업데이트 진행
        mRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (ScheduleData data : mDataset) {
                    callRouteData(data);
                }
            }
        });
    }

    private void callRouteData(final ScheduleData data) {

        double lng = CurrentLocation.getCurrentLocation()[0];
        double lat = CurrentLocation.getCurrentLocation()[1];

        Call<RouteData> routeDataCall = AppController.getRouteInfo()
                .getTransportInfo(lng, lat, data.getLongitude(), data.getLatitude());

        routeDataCall.enqueue(new Callback<RouteData>() {
            @Override
            public void onResponse(Call<RouteData> call, Response<RouteData> response) {

                if (response.isSuccessful()) {

                    RouteData result = response.body();
                    final int totalTime = result.getResult().getPath()[0].getInfo().getTotalTime();

                    mRealm.beginTransaction();
                    ScheduleData obj = mRealm.where(ScheduleData.class).equalTo("_id", data.get_id()).findFirst();
                    obj.setTotalTime(totalTime);
                    mRealm.commitTransaction();

                } else {
                    Log.e("CHECK_FAIL_RETROFIT", "----------- fail to get data");
                }
            }

            @Override
            public void onFailure(Call<RouteData> call, Throwable t) {

                t.printStackTrace();
                Log.e("CHECK_FAIL_SERVER", "----------- server access failure");
            }
        });
    }
}
