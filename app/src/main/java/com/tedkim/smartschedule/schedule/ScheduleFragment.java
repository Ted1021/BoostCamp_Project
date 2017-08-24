package com.tedkim.smartschedule.schedule;


import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
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

public class ScheduleFragment extends Fragment implements View.OnClickListener, OnTrafficInfoListener {

    // fragment view components
    SwipeRefreshLayout mRefreshLayout;
    ImageButton mScheduleSetting;
    LinearLayout mNoSchedule;
    TextView mToday;
    Date mDate;

    // realm components
    Realm mRealm;
    OrderedRealmCollectionChangeListener<RealmResults<ScheduleData>> mRealmCollectionListener;

    // recyclerView adapter components
    RealmResults<ScheduleData> mDataset;
    ScheduleRouteListAdapter mAdapter;
    RecyclerView mScheduleList;
    RecyclerView.LayoutManager mLayoutManager;

    // check device location
    Location mCurrentLocation;

    // traffic data type enum
    private static int MAX_ROUTE_PATH = 5;
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

        initView(view);
        initRealm();
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
        mScheduleSetting = (ImageButton) view.findViewById(R.id.imageButton_scheduleSetting);
        mScheduleSetting.setOnClickListener(this);
    }

    private void initRealm() {

        // init Realm database
        mRealm = Realm.getDefaultInstance();

        // init Realm data collection (=RealmResults) listener
        mRealmCollectionListener = new OrderedRealmCollectionChangeListener<RealmResults<ScheduleData>>() {
            @Override
            public void onChange(RealmResults<ScheduleData> scheduleDatas, OrderedCollectionChangeSet changeSet) {

                if (mDataset.isEmpty()) {
                    mNoSchedule.setVisibility(View.VISIBLE);
                } else {
                    mNoSchedule.setVisibility(View.GONE);
                }

                // 데이터셋의 삽입이 발생한 schedule data에 대해 경로 업데이트를 진행
                for (int position : changeSet.getInsertions()) {

                    mRefreshLayout.setRefreshing(true);
                    mCurrentLocation = CurrentLocation.getLocation(getContext());

                    // 스케줄의 위치를 현재 '디바이스' 의 위치로 변환
                    mRealm.beginTransaction();
                    scheduleDatas.get(position).setCurrentLongitude(mCurrentLocation.getLongitude());
                    scheduleDatas.get(position).setCurrentLatitude(mCurrentLocation.getLatitude());
                    mRealm.commitTransaction();

                    callRouteData(scheduleDatas.get(position));
                }
            }
        };
    }

    private void setRecyclerView() {

        mDataset = mRealm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(mDate)).findAll().sort("startTime");
        mAdapter = new ScheduleRouteListAdapter(mDataset, true, getContext());
//        mAdapter.onTrafficInfoClickListener();
//        mAdapter.onBeforeTimeChangeListener(this);
        mScheduleList.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);

        if (mDataset.isEmpty()) {
            mNoSchedule.setVisibility(View.VISIBLE);
        } else {
            mNoSchedule.setVisibility(View.GONE);
        }

        mDataset.addChangeListener(mRealmCollectionListener);
    }

    private void setAction() {

        // 오늘자 '모든' 스케줄에 대한 업데이트 진행
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                // 모든 정보가 업데이트 상태인지를 판단하는 flag
                Boolean isUpdated = true;

                // 오늘자 스케줄이 하나라도 있다면,
                if (mDataset.size() != 0) {

                    // 현재 좌표 호출
                    mCurrentLocation = CurrentLocation.getLocation(getContext());

                    // 스케줄에 저장 된 최근 위치와 업데이트 된 현재 위치를 비교해 선별적으로 서버에 접근
                    for (ScheduleData data : mDataset) {
                        Log.e("CHECK_LOCATION", "schedule fragment >>>> Longitude : " + mCurrentLocation.getLongitude() + "/" + data.getCurrentLongitude() + " / latitude : " + (data.getCurrentLatitude() + "/" + mCurrentLocation.getLatitude()));
                        if (data.getCurrentLongitude() != mCurrentLocation.getLongitude() || data.getCurrentLatitude() != mCurrentLocation.getLatitude()
                                || data.routeInfoList.size() == 0) {

                            isUpdated = false;

                            // 디바이스의 위치를 스케줄의 위치로 변환
                            mRealm.beginTransaction();

                            data.setCurrentLongitude(mCurrentLocation.getLongitude());
                            data.setCurrentLatitude(mCurrentLocation.getLatitude());

                            if (data.routeInfoList.size() != 0) {

                                // 가장 최하위에 있는 객체부터 순차적으로 삭제
                                RealmResults<RouteSeqData> routeSeqDatas = mRealm.where(RouteSeqData.class).equalTo("_id", data.get_id()).findAll();
                                routeSeqDatas.deleteAllFromRealm();

                                RealmResults<RouteInfo> routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll();
                                routeInfos.deleteAllFromRealm();
                            }
                            mRealm.commitTransaction();

                            // 변환 후 이동 정보 호출
                            callRouteData(data);
                        }
                    }

                    // 모든 정보가 최신상태라면 refresh 비활성화
                    if (isUpdated) {
                        Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.message_is_updated, Snackbar.LENGTH_LONG).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                }

                // 스케줄 데이터가 없는 경우
                else {
                    Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.error_message_no_data, Snackbar.LENGTH_LONG).show();
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    private void callRouteData(final ScheduleData data) {

        Log.d("CHECK_UPDATE_DATA", "schedule fragment >>>>>>>>>>>> " + data.get_id());
        // 현재 위치와 스케줄 상의 위치를 입력 받아 서버에 요청
        Call<RouteData> routeDataCall = AppController.getRouteInfo()
                .getTransportInfo(data.getCurrentLongitude(), data.getCurrentLatitude(), data.getLongitude(), data.getLatitude());

        routeDataCall.enqueue(new Callback<RouteData>() {
            @Override
            public void onResponse(Call<RouteData> call, Response<RouteData> response) {

                if (response.isSuccessful()) {

                    int maxPath = MAX_ROUTE_PATH;
                    RouteData result = response.body();

                    mRealm.beginTransaction();
                    ScheduleData obj = mRealm.where(ScheduleData.class).equalTo("_id", data.get_id()).findFirst();

//                    obj.setBusCount(result.getResult().getBusCount());
//                    obj.setSubwayCount(result.getResult().getSubwayCount());
//                    obj.setSubwayBusCount(result.getResult().getSubwayBusCount());

                    if (result.getResult().getPath().length < maxPath) {
                        maxPath = result.getResult().getPath().length;
                    }
                    for (int i = 0; i < maxPath; i++) {

                        RouteData.Result.Path path = result.getResult().getPath()[i];

                        RouteInfo routeInfo = mRealm.createObject(RouteInfo.class);
                        routeInfo.set_id(data.get_id());
                        routeInfo.setDepartTime(DateConvertUtil.calDateMin(obj.getStartTime(), path.getInfo().getTotalTime()));
                        routeInfo.setArriveTime(obj.getEndTime());
                        routeInfo.setTotalTime(path.getInfo().getTotalTime());
                        routeInfo.setPayment(path.getInfo().getPayment());
                        routeInfo.setBusTransitCount(path.getInfo().getBusTransitCount());
                        routeInfo.setSubwayTransitCount(path.getInfo().getSubwayTransitCount());
                        routeInfo.setTotalDistance(path.getInfo().getTotalDistance());
                        routeInfo.setTotalTransitCount(path.getInfo().getBusTransitCount() + path.getInfo().getSubwayTransitCount());
                        if(i==0) {
                            routeInfo.setSelected(true);
                        }
                        routeInfo.setSelected(false);

                        for (RouteData.Result.Path.SubPath subPath : path.getSubPath()) {

                            RouteSeqData seqData = mRealm.createObject(RouteSeqData.class);
                            seqData.set_id(data.get_id());
                            seqData.setTrafficType(subPath.getTrafficType());

                            if (subPath != null) {
                                // subway
                                if (seqData.getTrafficType() == TYPE_SUBWAY) {
                                    seqData.setSubwayName(subPath.getLane().getName());
                                    seqData.setSubwayType(subPath.getLane().getSubwayCode());
                                }
                                // bus
                                else if (seqData.getTrafficType() == TYPE_BUS) {
                                    seqData.setBusName(subPath.getLane().getBusNo());
                                    seqData.setBusType(subPath.getLane().getType());
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageButton_scheduleSetting:
                showBottomSheet();
                break;
        }
    }

    private void showBottomSheet() {

        new BottomSheet.Builder(getActivity()).sheet(R.menu.bottom_sheet).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case R.id.remove_schedule:

                        break;

                    case R.id.sort_time:
                        mDataset = mDataset.sort("startTime");
                        break;

                    case R.id.sort_distance:
                        mDataset = mDataset.sort("distance");
                        break;

                    case R.id.sort_departure:
                        mDataset = mDataset.sort("expectedDepartTime");
                        break;
                }
                mAdapter.updateData(mDataset);

            }
        }).show();
    }

    @Override
    public void onTrafficInfoClickListener(ScheduleRouteListAdapter.ViewHolder viewHolder, RouteInfo routeInfo) {

    }

    @Override
    public void onBeforeTimeChangeListener(int beforeTime) {

    }
}
