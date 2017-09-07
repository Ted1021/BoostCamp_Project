package com.tedkim.smartschedule.schedule;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
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

import com.cocosw.bottomsheet.BottomSheet;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.BeforeTimeMessage;
import com.tedkim.smartschedule.model.RouteData;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.RouteInfoMessage;
import com.tedkim.smartschedule.model.RouteSeqData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.service.alarm.AlarmService;
import com.tedkim.smartschedule.service.notification.NotificationService;
import com.tedkim.smartschedule.service.refresh.RefreshMessage;
import com.tedkim.smartschedule.service.refresh.RefreshService;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.CurrentLocation;
import com.tedkim.smartschedule.util.DateConvertUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.Date;

import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

/**
 * @author 김태원
 * @file ScheduleFragment.java
 * @brief Show today & tomorrow schedules with departure time
 * @date 2017.07.31
 */

public class ScheduleFragment extends Fragment {

    // fragment view components
    SwipeRefreshLayout mRefreshLayout;
    ImageButton mScheduleSetting;
    LinearLayout mNoSchedule;
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDetach() {
        mRealm.close();
        super.onDetach();
    }

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

    // Schedule Fragment View 컴포넌트 초기화
    private void initView(View view) {

        long now = System.currentTimeMillis();
        mDate = new Date(now);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mNoSchedule = (LinearLayout) view.findViewById(R.id.layout_no_schedule);
        mScheduleList = (RecyclerView) view.findViewById(R.id.recyclerView_scheduleList);
        mScheduleSetting = (ImageButton) view.findViewById(R.id.imageButton_scheduleSetting);
    }

    // RecyclerView 제어를 위한 Realm Instance 초기화
    private void initRealm() {

        // init Realm database
        mRealm = Realm.getDefaultInstance();

        // Realm data collection 변경 리스너 부착
        mRealmCollectionListener = new OrderedRealmCollectionChangeListener<RealmResults<ScheduleData>>() {
            @Override
            public void onChange(RealmResults<ScheduleData> scheduleDatas, OrderedCollectionChangeSet changeSet) {

                if (mDataset.isEmpty()) {
                    mNoSchedule.setVisibility(View.VISIBLE);
                } else {
                    mNoSchedule.setVisibility(View.GONE);
                }

                // 데이터셋의 '삽입'이 발생한 schedule data에 대해 자동으로 경로 업데이트를 진행
                for (int position : changeSet.getInsertions()) {

                    mCurrentLocation = CurrentLocation.getLocation(getContext());

                    // 스케줄의 위치를 현재 '디바이스' 의 위치로 변환
                    mRealm.beginTransaction();
                    scheduleDatas.get(position).setCurrentLongitude(mCurrentLocation.getLongitude());
                    scheduleDatas.get(position).setCurrentLatitude(mCurrentLocation.getLatitude());
                    mRealm.commitTransaction();

                    callRouteData(scheduleDatas.get(position).get_id());
                }
            }
        };
    }

    // Schedule RecyclerView 초기화
    private void setRecyclerView() {

        // 오늘자 스케줄의 '일정 순'으로 정렬된 데이터를 세팅한다
        mDataset = mRealm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(mDate)).findAll().sort("startTime");
        mAdapter = new ScheduleRouteListAdapter(mDataset, true, getContext());
        mScheduleList.setAdapter(mAdapter);

        // Layout Manager 설정
        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);

        // 오늘자 일정이 없으면 NoSchedule View 를 보여준다
        if (mDataset.isEmpty()) {
            mNoSchedule.setVisibility(View.VISIBLE);
        } else {
            mNoSchedule.setVisibility(View.GONE);
        }

        // initRealm() 에서 정의했던 Realm Collection Listener 를 부착한다
        mDataset.addChangeListener(mRealmCollectionListener);
    }

    // Schedule Fragment View Item 들의 동작을 정의
    private void setAction() {

        // 스케줄 정렬 및 삭제 메뉴 실행
        mScheduleSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheet();
            }
        });

        // 오늘자 '모든' 스케줄에 대한 업데이트 진행
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAllSchedules();
            }
        });
    }

    // 스케줄 설정 메뉴
    private void showBottomSheet() {

        new BottomSheet.Builder(getActivity()).sheet(R.menu.schedule_bottom_sheet).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent;
                switch (which) {

                    // 지난 스케줄 삭제
                    case R.id.remove_schedule:

                        break;

                    // 일정 순 정렬
                    case R.id.sort_time:
                        mDataset = mDataset.sort("startTime");
                        break;

                    // 거리 순 정렬
                    case R.id.sort_distance:
                        mDataset = mDataset.sort("distance");
                        break;

                    // 알림 비활성화
                    case R.id.remove_notification:
                        intent = new Intent(getActivity(), NotificationService.class);
                        getActivity().stopService(intent);
                        break;

                    // 자동 새로고침 비활성화
                    case R.id.remove_refresh:
                        intent = new Intent(getActivity(), RefreshService.class);
                        getActivity().stopService(intent);
                        break;
                }

                // 변경 된 정렬 로직에 따라 RecyclerView 업데이트
                mAdapter.updateData(mDataset);
            }
        }).show();
    }

    // 오늘자 '모든' 스케줄에 대한 업데이트 진행로직
    private void refreshAllSchedules() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        // 모든 정보가 업데이트 상태인지를 판단하는 flag
                        Boolean isUpdated = true;
                        RealmResults<ScheduleData> dataset = realm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(mDate)).findAll().sort("startTime");

                        // 오늘자 스케줄이 하나라도 있다면,
                        if (dataset.size() != 0) {

                            // 현재 좌표 호출
                            mCurrentLocation = CurrentLocation.getLocation(getContext());

                            // 이동경로 정보가 없거나, 위치에 변동이 생긴 스케줄에 대해 업데이트 진행
                            for (ScheduleData data : dataset) {

                                AlarmService.removeAlarm(getContext(), data.get_id());
                                Date expectedDepartTime = DateConvertUtil.calDateMin(data.getStartTime(), data.getBeforeTime() + data.getTotalTime());
                                data.setExpectedDepartTime(expectedDepartTime);
                                AlarmService.setAlarm(getContext(), data.get_id());

                                Log.e("CHECK_LOCATION", "schedule fragment >>>> Longitude : " + mCurrentLocation.getLongitude() + "/" + data.getCurrentLongitude() + " / latitude : " + (data.getCurrentLatitude() + "/" + mCurrentLocation.getLatitude()));
                                if (data.routeInfoList.size() == 0 || data.getCurrentLongitude() != mCurrentLocation.getLongitude() || data.getCurrentLatitude() != mCurrentLocation.getLatitude()) {

                                    isUpdated = false;

                                    data.setCurrentLongitude(mCurrentLocation.getLongitude());
                                    data.setCurrentLatitude(mCurrentLocation.getLatitude());

                                    // 세부 이동 경로 갱신 작업 (모든 정보 삭제이후 재설정)
                                    if (data.routeInfoList.size() != 0) {

                                        // 가장 하위 정보인 '환승 정보'부터 상위 정보인 '이동 경로'까지 순차적으로 삭제
                                        RealmResults<RouteSeqData> routeSeqDatas = realm.where(RouteSeqData.class).equalTo("_id", data.get_id()).findAll();
                                        routeSeqDatas.deleteAllFromRealm();

                                        RealmResults<RouteInfo> routeInfos = realm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll();
                                        routeInfos.deleteAllFromRealm();
                                    }

                                    // 삭제이후 이동 정보 호출
                                    callRouteData(data.get_id());
                                }
                            }

                            // 모든 정보가 최신상태라면 refresh 비활성화
                            if (isUpdated) {
                                publishProgress();
                            }
                        }
                    }
                });
                realm.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                // 모든 정보가 최신 정보임을 사용자에게 알림
                Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.message_is_updated, Snackbar.LENGTH_LONG).show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                // 변경 된 이동정보를 적용
                mAdapter.notifyDataSetChanged();
                mRefreshLayout.setRefreshing(false);
            }
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);

    }

    // 단일 스케줄에 대한 업데이트 진행로직
    private void callRouteData(final String id) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.setRefreshing(true);
                    }
                });
            }

            // Retrofit Call 과 Realm Transaction 을 모두 동기방식으로 호출해 doInBackground() 에서 묶어서 작업함
            @Override
            protected Void doInBackground(Void... params) {

                // 갱신할 스케줄 데이터를 받아 옴
                Realm realm = Realm.getDefaultInstance();
                ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", id).findFirst();

                // 현재 위치와 스케줄 상의 위치를 입력 받아 서버에 요청
                Call<RouteData> routeDataCall = AppController.getRouteInfo()
                        .getTransportInfo(data.getCurrentLongitude(), data.getCurrentLatitude(), data.getLongitude(), data.getLatitude());

                if(data.getCurrentLongitude() == data.getLongitude() && data.getCurrentLatitude() == data.getLatitude()){
                    publishProgress();
                    return null;
                }

                // 서버로부터 전달받은 데이터를 기반으로 업데이트 시작
                // 최대 5개까지의 서로 다른 경로를 가져오며,
                // 스케줄 정보 > 이동경로 정보 > 세부환승 정보 순으로 데이터를 삽입 또는 갱신

                realm.beginTransaction();
                try {

                    int maxPath = MAX_ROUTE_PATH;
                    RouteData result = routeDataCall.execute().body();

                    // "스케줄 정보" 삽입
                    data.setBeforeTime(0);
                    data.setTotalTime(result.getResult().getPath()[0].getInfo().getTotalTime());
                    data.setDistance(result.getResult().getPath()[0].getInfo().getTotalDistance());

                    // 이동경로수가 5개 미만일 경우, 최대 이동경로 수를 조정
                    if (result.getResult().getPath().length < maxPath) {
                        maxPath = result.getResult().getPath().length;
                    }

                    // "이동경로 정보" 삽입
                    for (int i = 0; i < maxPath; i++) {

                        RouteData.Result.Path path = result.getResult().getPath()[i];

                        RouteInfo routeInfo = realm.createObject(RouteInfo.class);
                        routeInfo.set_id(data.get_id());
                        routeInfo.setDepartTime(DateConvertUtil.calDateMin(data.getStartTime(), path.getInfo().getTotalTime()));
                        routeInfo.setArriveTime(data.getEndTime());
                        routeInfo.setTotalTime(path.getInfo().getTotalTime());
                        routeInfo.setPayment(path.getInfo().getPayment());
                        routeInfo.setBusTransitCount(path.getInfo().getBusTransitCount());
                        routeInfo.setSubwayTransitCount(path.getInfo().getSubwayTransitCount());
                        routeInfo.setTotalDistance(path.getInfo().getTotalDistance());
                        routeInfo.setTotalTransitCount(path.getInfo().getBusTransitCount() + path.getInfo().getSubwayTransitCount());

                        // "세부환승 정보" 삽입
                        for (RouteData.Result.Path.SubPath subPath : path.getSubPath()) {

                            RouteSeqData seqData = realm.createObject(RouteSeqData.class);

                            seqData.set_id(data.get_id());
                            seqData.setTrafficType(subPath.getTrafficType());

                            // 도보, 버스, 지하철과 같은 이동수단 유형에따라 다르게 저장
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
                                // or walk
                            }
                            routeInfo.routeSequence.add(seqData);
                        }
                        data.routeInfoList.add(routeInfo);
                    }

                } catch (IOException e) {
                    // 네트워크 상황이 좋지 못하면 특정 메세지를 통해 사용자에게 안내
                    e.printStackTrace();
                    publishProgress();
                }

                realm.commitTransaction();
                realm.close();
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... values) {
                super.onProgressUpdate(values);
                Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.error_message_fail_server, Snackbar.LENGTH_LONG).show();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mRefreshLayout.setRefreshing(false);
            }
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    // Event Bus 설정
    // From ScheduleRouteAdapter (line 251)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBeforeTimeMessageEvent(final BeforeTimeMessage event) {

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", event.getId()).findFirst();
                data.setBeforeTime(event.getBeforeTime());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    // From TrafficInfoListAdapter (line 94)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRouteInfoMessageEvent(final RouteInfoMessage event) {

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", event.getId()).findFirst();
                data.setTotalTime(event.getTotalTime());
                data.setDistance(event.getDistance());
            }
        });
        mAdapter.notifyDataSetChanged();
    }

    // From Refresh Service (line 147)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartRefreshEvent(RefreshMessage event) {
        if (event.getReq() == 0) {
            Log.d("CHECK_REFRESH", ">>>>>>> start refresh");
            mRefreshLayout.setRefreshing(true);
        } else {
            Log.d("CHECK_REFRESH", ">>>>>>> end refresh");
            mAdapter.notifyDataSetChanged();
            mRefreshLayout.setRefreshing(false);
        }
    }
}
