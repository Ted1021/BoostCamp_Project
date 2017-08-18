package com.tedkim.smartschedule.schedule;


import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.RouteData;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.RouteSeqData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.CurrentLocation;
import com.tedkim.smartschedule.util.DateConvertUtil;

import java.util.Date;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tedkim.smartschedule.util.DateConvertUtil.TYPE_KOR;

/**
 * @author 김태원
 * @file ScheduleFragment.java
 * @brief Show today & tomorrow schedules with departure time
 * @date 2017.07.31
 */

public class ScheduleFragment extends Fragment {

    SwipeRefreshLayout mRefreshLayout;
    LinearLayout mNoSchedule;
    TextView mToday;
    Date mDate;

    Realm mRealm;

    RealmResults<ScheduleData> mDataset;
    ScheduleRouteListAdapter mAdapter;
    RecyclerView mScheduleList;
    RecyclerView.LayoutManager mLayoutManager;

    Location mCurrentLocation;

    private static int MAX_ROUTE_INFO = 3;
    private static int TYPE_SUBWAY = 1;
    private static int TYPE_BUS = 2;
    private static int TYPE_WALK = 3;

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

        long now = System.currentTimeMillis();
        mDate = new Date(now);

        mToday = (TextView) view.findViewById(R.id.textView_today);
        mToday.setText(DateConvertUtil.date2string(mDate, TYPE_KOR));
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mNoSchedule = (LinearLayout) view.findViewById(R.id.layout_no_schedule);
        mScheduleList = (RecyclerView) view.findViewById(R.id.recyclerView_scheduleList);
    }

    private void setRecyclerView() {

        mDataset = mRealm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(mDate)).findAll();
        mDataset = mDataset.sort("startTime");

        mAdapter = new ScheduleRouteListAdapter(mDataset, true, getContext());
        mScheduleList.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);

        if(mDataset.isEmpty()){
            mNoSchedule.setVisibility(View.VISIBLE);
        } else{
            mNoSchedule.setVisibility(View.GONE);
        }

        mDataset.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ScheduleData>>() {
            @Override
            public void onChange(RealmResults<ScheduleData> scheduleDatas, OrderedCollectionChangeSet changeSet) {

                if(mDataset.isEmpty()){
                    mNoSchedule.setVisibility(View.VISIBLE);
                } else{
                    mNoSchedule.setVisibility(View.GONE);
                }

                Log.d("CHECK_CHANGES", ">>>>>>>>> onChange " + changeSet.getChanges().length);

                // 데이터셋의 삽입이 발생한 schedule data에 대해 경로 업데이트를 진행
                for (int position : changeSet.getInsertions()) {

                    mRefreshLayout.setRefreshing(true);
                    mCurrentLocation = CurrentLocation.getLocation(getContext());
                    Log.e("CHECK_ENTER", ">>>>>>>>> Location " + mCurrentLocation);

                    // 스케줄의 위치를 디바이스의 위치로 변환
                    mRealm.beginTransaction();
                    scheduleDatas.get(position).setCurrentLongitude(mCurrentLocation.getLongitude());
                    scheduleDatas.get(position).setCurrentLatitude(mCurrentLocation.getLatitude());
                    mRealm.commitTransaction();

                    callRouteData(scheduleDatas.get(position));
                }
            }
        });
    }

    private void setAction() {

        // 모든 오늘자 스케줄에 대한 업데이트 진행
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (mDataset.size() != 0) {
                    // 현재 좌표 호출
                    mCurrentLocation = CurrentLocation.getLocation(getContext());

                    // 스케줄에 저장 된 최근 위치와 업데이트 된 현재 위치를 비교해 선별적으로 서버에 접근
                    for (ScheduleData data : mDataset) {

                        if (data.getLongitude() != mCurrentLocation.getLongitude() || data.getLatitude() != mCurrentLocation.getLatitude()) {
                            // 디바이스의 위치를 스케줄의 위치로 변환
                            mRealm.beginTransaction();
                            data.setCurrentLongitude(mCurrentLocation.getLongitude());
                            data.setCurrentLatitude(mCurrentLocation.getLatitude());
                            mRealm.commitTransaction();
                        }
                        // 변환 후 이동 정보 호출
                        callRouteData(data);
                    }
                } else {
                    Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.error_message_no_data, Snackbar.LENGTH_LONG).show();
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    // TODO - 로직 전체로 봤을 때, 3중 For 문임 ... 굉장히 안좋음 - Realm 의 제한적 구조상 아마도 해결법은 서버단에서 데이터를 재가공해서 필요한 정보만 return 해주는 방법밖에 없음
    private void callRouteData(final ScheduleData data) {

        Log.d("CHECK_UPDATE_DATA", "schedule fragment >>>>>>>>>>>> " + data.get_id());
        // 현재 위치와 스케줄 상의 위치를 입력 받아 서버에 요청
        Call<RouteData> routeDataCall = AppController.getRouteInfo()
                .getTransportInfo(data.getCurrentLongitude(), data.getCurrentLatitude(), data.getLongitude(), data.getLatitude());

        routeDataCall.enqueue(new Callback<RouteData>() {
            @Override
            public void onResponse(Call<RouteData> call, Response<RouteData> response) {

                if (response.isSuccessful()) {

                    RouteData result = response.body();
                    Log.e("CHECK_RESULT", "schedule fragment ++++++++++ " + result.getResult().getPath().length);

                    mRealm.beginTransaction();
                    ScheduleData obj = mRealm.where(ScheduleData.class).equalTo("_id", data.get_id()).findFirst();

                    // 최대 3개의 경로를 가져 옴
                    for (int i = 0; i < MAX_ROUTE_INFO; i++) {
                        RouteData.Result.Path path = result.getResult().getPath()[i];

                        RouteInfo routeInfo = mRealm.createObject(RouteInfo.class);
                        routeInfo.set_id(data.get_id());
                        Log.e("CHECK_ROUTE_INFO", "+++++++++++++++ " + i + ") " + routeInfo.get_id());
                        routeInfo.setDepartTime(DateConvertUtil.calDateMin(obj.getStartTime(), path.getInfo().getTotalTime()));
                        routeInfo.setArriveTime(obj.getEndTime());
                        routeInfo.setTotalTime(path.getInfo().getTotalTime());
                        routeInfo.setPayment(path.getInfo().getPayment());
                        routeInfo.setBusStationCount(path.getInfo().getBusStationCount());
                        routeInfo.setSubwayStationCount(path.getInfo().getSubwayStationCount());

                        for (RouteData.Result.Path.SubPath subPath : path.getSubPath()) {

                            RouteSeqData seqData = mRealm.createObject(RouteSeqData.class);
                            seqData.set_id(data.get_id());
                            Log.e("CHECK_ROUTE_SEQ", "++++++++++ " + i + ") " + seqData.get_id());
                            seqData.setTrafficType(subPath.getTrafficType());
                            if (subPath != null) {
                                // subway
                                if (seqData.getTrafficType() == TYPE_SUBWAY) {
                                    seqData.setBusName(subPath.getLane().getName());
                                }
                                // bus
                                else if (seqData.getTrafficType() == TYPE_BUS) {
                                    seqData.setBusName(subPath.getLane().getBusNo());
                                }
                            }
                            routeInfo.routeSequence.add(seqData);
                        }
                        obj.routeInfoList.add(routeInfo);
                    }
                    mRealm.commitTransaction();
                } else {
                    Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.error_message_fail_data, Snackbar.LENGTH_LONG).show();
                    Log.e("CHECK_FAIL_RETROFIT", "schedule fragment ----------- fail to get data");
                }

                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<RouteData> call, Throwable t) {

                t.printStackTrace();
                Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.error_message_fail_server, Snackbar.LENGTH_LONG).show();
                mRefreshLayout.setRefreshing(false);
                Log.e("CHECK_FAIL_SERVER", "schedule fragment ----------- server access failure");
            }
        });
    }
}
