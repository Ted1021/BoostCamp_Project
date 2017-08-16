package com.tedkim.smartschedule.schedule;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.RouteData;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.RouteSeqData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.DateConvertUtil;

import java.util.Date;

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

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
        mScheduleList = (RecyclerView) view.findViewById(R.id.recyclerView_scheduleList);
    }

    private void setRecyclerView() {

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        mDataset = mRealm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(date)).findAll();
        mDataset = mDataset.sort("startTime");

        mAdapter = new ScheduleRouteListAdapter(mDataset, true, getContext());
        mScheduleList.setAdapter(mAdapter);

        mLayoutManager = new LinearLayoutManager(getContext());
        mScheduleList.setLayoutManager(mLayoutManager);
    }

    private void setAction() {

        // 모든 오늘자 스케줄에 대한 업데이트 진행
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(mDataset.size() != 0){
                    // 현재 좌표 호출
                    LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                    locationManager.removeUpdates(mLocationListener);

                    // splash 에서 권한 허용 되었 때,
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
                    }
                    // splash 에서 권한 허용을 하지 않았을 때
                    else {

                        // TODO - 스낵바에 버튼을 달아 설정 화면으로 분기 하도록 해야 함
                        Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), R.string.error_message_location, Snackbar.LENGTH_LONG).show();
                        mRefreshLayout.setRefreshing(false);
                    }
                }
                else{
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    // TODO - 로직 전체로 봤을 때, 3중 For 문임 ... 굉장히 안좋음 - Realm 의 제한적 구조상 아마도 해결법은 서버단에서 데이터를 재가공해서 필요한 정보만 return 해주는 방법밖에 없음
    private void callRouteData(final ScheduleData data) {

        Log.d("CHECK_UPDATE_DATA", ">>>>>>>>>>>> "+data.get_id());
        // 현재 위치와 스케줄 상의 위치를 입력 받아 서버에 요청
        Call<RouteData> routeDataCall = AppController.getRouteInfo()
                .getTransportInfo(data.getCurrentLongitude(), data.getCurrentLatitude(), data.getLongitude(), data.getLatitude());

        routeDataCall.enqueue(new Callback<RouteData>() {
            @Override
            public void onResponse(Call<RouteData> call, Response<RouteData> response) {

                if (response.isSuccessful()) {

                    RouteData result = response.body();

                    mRealm.beginTransaction();
                    ScheduleData obj = mRealm.where(ScheduleData.class).equalTo("_id", data.get_id()).findFirst();

                    for(int i=0; i<MAX_ROUTE_INFO; i++){

                        RouteData.Result.Path path = result.getResult().getPath()[i];

                        RouteInfo routeInfo = mRealm.createObject(RouteInfo.class);

                        routeInfo.setDepartTime(DateConvertUtil.calDateMin(obj.getStartTime(),path.getInfo().getTotalTime()));
                        routeInfo.setArriveTime(obj.getEndTime());
                        routeInfo.setTotalTime(path.getInfo().getTotalTime());
                        routeInfo.setPayment(path.getInfo().getPayment());
                        routeInfo.setBusStationCount(path.getInfo().getBusStationCount());
                        routeInfo.setSubwayStationCount(path.getInfo().getSubwayStationCount());

                        for(RouteData.Result.Path.SubPath subPath : path.getSubPath()){

                            RouteSeqData seqData = mRealm.createObject(RouteSeqData.class);

                            seqData.setTrafficType(subPath.getTrafficType());
                            if(subPath != null){
                                // subway
                                if(seqData.getTrafficType() == TYPE_SUBWAY){

                                    seqData.setBusName(subPath.getLane().getName());
                                }
                                // bus
                                else if(seqData.getTrafficType() == TYPE_BUS){
                                    seqData.setBusName(subPath.getLane().getBusNo());
                                }
                            }
                            routeInfo.routeSequence.add(seqData);
                        }
                        obj.routeInfoList.add(routeInfo);
                    }
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

    LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            mCurrentLocation = location;

            // 스케줄에 저장 된 최근 위치와 업데이트 된 현재 위치를 비교해 선별적으로 서버에 접근
            for (ScheduleData data : mDataset) {

                Log.d("CHECK_COMPARE_LONGITUDE", data.getCurrentLongitude() + " / " + mCurrentLocation.getLongitude());
                Log.d("CHECK_COMPARE_LATITUDE", data.getCurrentLatitude() + " / " + mCurrentLocation.getLatitude());

                if(data.getCurrentLongitude() != mCurrentLocation.getLongitude() || data.getCurrentLatitude() != mCurrentLocation.getLatitude()){

                    mRealm.beginTransaction();
                    data.setCurrentLongitude(mCurrentLocation.getLongitude());
                    data.setCurrentLatitude(mCurrentLocation.getLatitude());
                    mRealm.commitTransaction();

                    callRouteData(data);
                }
            }
            mRefreshLayout.setRefreshing(false);
            lm.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    };
}
