package com.tedkim.smartschedule.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.tedkim.smartschedule.model.RouteData;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.RouteSeqData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.CurrentLocation;
import com.tedkim.smartschedule.util.DateConvertUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

import static com.tedkim.smartschedule.service.RefreshThread.REQ_REFRESH_SCHEDULE;

public class RefreshService extends Service {

    RefreshThread mRefreshThread;
    RefreshServiceHandler mRefreshServiceHandler;
    Location mCurrentLocation;

    // traffic data type enum
    private static int MAX_ROUTE_PATH = 5;
    private static int TYPE_SUBWAY = 1;
    private static int TYPE_BUS = 2;
    private static int TYPE_WALK = 3;

    public RefreshService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mRefreshThread.stopForever();
        mRefreshThread = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mRefreshServiceHandler = new RefreshServiceHandler();
        mRefreshThread = new RefreshThread(mRefreshServiceHandler);
        mRefreshThread.start();
        return START_STICKY;

    }

    class RefreshServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            if(msg.what == REQ_REFRESH_SCHEDULE){
                refreshAllSchedules();
                Log.d("CHECK_REFRESH_SERVICE", "refresh service >>> 새로고침 수행");
            }
        }
    }

    private void refreshAllSchedules() {

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                EventBus.getDefault().post(new RefreshMessage(0));
            }

            @Override
            protected Void doInBackground(Void... params) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        // 모든 정보가 업데이트 상태인지를 판단하는 flag
                        Boolean isUpdated = true;
                        RealmResults<ScheduleData> dataset = realm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(new Date(System.currentTimeMillis()))).findAll().sort("startTime");

                        // 오늘자 스케줄이 하나라도 있다면,
                        if (dataset.size() != 0) {

                            // 현재 좌표 호출
                            mCurrentLocation = CurrentLocation.getLocation(getApplicationContext());

                            // 스케줄에 저장 된 최근 위치와 업데이트 된 현재 위치를 비교해 선별적으로 서버에 접근
                            for (ScheduleData data : dataset) {
                                Log.e("CHECK_LOCATION", "schedule fragment >>>> Longitude : " + mCurrentLocation.getLongitude() + "/" + data.getCurrentLongitude() + " / latitude : " + (data.getCurrentLatitude() + "/" + mCurrentLocation.getLatitude()));
                                if (data.getCurrentLongitude() != mCurrentLocation.getLongitude() || data.getCurrentLatitude() != mCurrentLocation.getLatitude()
                                        || data.routeInfoList.size() == 0) {

                                    isUpdated = false;

                                    data.setCurrentLongitude(mCurrentLocation.getLongitude());
                                    data.setCurrentLatitude(mCurrentLocation.getLatitude());

                                    if (data.routeInfoList.size() != 0) {

                                        // 가장 최하위에 있는 객체부터 순차적으로 삭제
                                        RealmResults<RouteSeqData> routeSeqDatas = realm.where(RouteSeqData.class).equalTo("_id", data.get_id()).findAll();
                                        routeSeqDatas.deleteAllFromRealm();

                                        RealmResults<RouteInfo> routeInfos = realm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll();
                                        routeInfos.deleteAllFromRealm();
                                    }
                                    // 변환 후 이동 정보 호출
                                    callRouteData(data.get_id());
                                }
                            }

                            // 모든 정보가 최신상태라면 refresh 비활성화
                            if (isUpdated) {
                            }
                        }
                    }
                });
                realm.close();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                EventBus.getDefault().post(new RefreshMessage(1));
            }
        }.execute();
    }

    private void callRouteData(final String id) {

        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {

                Realm realm = Realm.getDefaultInstance();
                ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", id).findFirst();

                // 현재 위치와 스케줄 상의 위치를 입력 받아 서버에 요청
                Call<RouteData> routeDataCall = AppController.getRouteInfo()
                        .getTransportInfo(data.getCurrentLongitude(), data.getCurrentLatitude(), data.getLongitude(), data.getLatitude());

                realm.beginTransaction();
                try {
                    int maxPath = MAX_ROUTE_PATH;
                    RouteData result = routeDataCall.execute().body();

                    data.setBeforeTime(0);
                    data.setTotalTime(result.getResult().getPath()[0].getInfo().getTotalTime());
                    data.setDistance(result.getResult().getPath()[0].getInfo().getTotalDistance());

                    if (result.getResult().getPath().length < maxPath) {
                        maxPath = result.getResult().getPath().length;
                    }

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
                        routeInfo.setSelected(false);
                        if (i == 0) {
                            routeInfo.setSelected(true);
                        }

                        for (RouteData.Result.Path.SubPath subPath : path.getSubPath()) {

                            RouteSeqData seqData = realm.createObject(RouteSeqData.class);

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
                        data.routeInfoList.add(routeInfo);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    publishProgress();
                }
                realm.commitTransaction();
                realm.close();
                return null;
            }
        }.execute();
    }
}
