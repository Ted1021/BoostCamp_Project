package com.tedkim.smartschedule.service.refresh;

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
import com.tedkim.smartschedule.service.alarm.AlarmService;
import com.tedkim.smartschedule.service.notification.NotificationMessage;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.CurrentLocation;
import com.tedkim.smartschedule.util.DateConvertUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;

public class RefreshService extends Service {

    RefreshThread mRefreshThread;
    RefreshServiceHandler mRefreshServiceHandler;
    Location mCurrentLocation;
    RealmResults<ScheduleData> mDataset;
    Realm mRealm;

    // traffic data type enum
    private static int MAX_ROUTE_PATH = 5;
    private static int TYPE_SUBWAY = 1;
    private static int TYPE_BUS = 2;
    private static int TYPE_WALK = 3;

    public RefreshService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
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
        mRealm.close();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mRealm = Realm.getDefaultInstance();
        mRefreshServiceHandler = new RefreshServiceHandler();
        mRefreshThread = new RefreshThread(mRefreshServiceHandler);
        mRefreshThread.start();
        return START_STICKY;
    }

    class RefreshServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            if (msg.what == RefreshThread.REQ_REFRESH_SCHEDULE) {

//                Realm realm = Realm.getDefaultInstance();
//                mDataset = realm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(new Date(System.currentTimeMillis()))).findAll().sort("startTime");
//                mDataset.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<ScheduleData>>() {
//                    @Override
//                    public void onChange(RealmResults<ScheduleData> scheduleDatas, OrderedCollectionChangeSet changeSet) {
//                        // 데이터셋의 '변경'이 발생한 schedule data에 대해 새로운 Notification set 을 설정
//                        for (int position : changeSet.getChanges()) {
//
//                            ScheduleData data = scheduleDatas.get(position);
//
//                            long interval = data.getExpectedDepartTime().getTime() - System.currentTimeMillis();
//                            long result = TimeUnit.MILLISECONDS.toMinutes(interval);
//                            Log.e("CHECK_NOTI", ">>>>>>>>> "+data.get_id());
//
//                            if (result >= 1 && result <= 10) {
//                                EventBus.getDefault().post(new NotificationMessage(scheduleDatas.get(position).get_id()));
//                            }
//                        }
//                    }
//                });
//                realm.close();

                refreshAllSchedules();
                Log.d("CHECK_REFRESH_SERVICE", "refresh service >>> 새로고침 수행");
            }
        }
    }

    private void refreshAllSchedules() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                EventBus.getDefault().post(new RefreshMessage(0));
            }

            @Override
            protected Void doInBackground(Void... params) {

                Realm realm = Realm.getDefaultInstance();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        // 모든 정보가 업데이트 상태인지를 판단하는 flag
                        Boolean isUpdated = true;
                        mDataset = realm.where(ScheduleData.class).equalTo("date", DateConvertUtil.date2string(new Date(System.currentTimeMillis()))).findAll().sort("startTime");

                        // 오늘자 스케줄이 하나라도 있다면,
                        if (mDataset.size() != 0) {

                            // 현재 좌표 호출
                            mCurrentLocation = CurrentLocation.getLocation(getApplicationContext());

                            // 스케줄에 저장 된 최근 위치와 업데이트 된 현재 위치를 비교해 선별적으로 서버에 접근
                            for (ScheduleData data : mDataset) {

                                AlarmService.removeAlarm(getApplicationContext(), data.get_id());
                                Date expectedDepartTime = DateConvertUtil.calDateMin(data.getStartTime(), data.getBeforeTime() + data.getTotalTime());
                                data.setExpectedDepartTime(expectedDepartTime);
                                AlarmService.setAlarm(getApplicationContext(), data.get_id());

                                long interval = expectedDepartTime.getTime() - System.currentTimeMillis();
                                long result = TimeUnit.MILLISECONDS.toMinutes(interval);

                                if (result >= 0 && result <= 10) {
                                    EventBus.getDefault().post(new NotificationMessage(data.get_id()));
                                }

                                Log.e("CHECK_LOCATION", "schedule fragment >>>> Longitude : " + mCurrentLocation.getLongitude() + "/" + data.getCurrentLongitude() + " / latitude : " + (data.getCurrentLatitude() + "/" + mCurrentLocation.getLatitude()));
                                if (data.routeInfoList.size() == 0 || data.getCurrentLongitude() != mCurrentLocation.getLongitude() || data.getCurrentLatitude() != mCurrentLocation.getLatitude()) {

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

                                // 모든 정보가 최신상태라면 refresh 비활성화
                                if (isUpdated) {

                                }
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
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }

    private void callRouteData(final String id) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                EventBus.getDefault().post(new RefreshMessage(0));
            }

            @Override
            protected Void doInBackground(Void... params) {

                Realm realm = Realm.getDefaultInstance();
                final ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", id).findFirst();

                // 현재 위치와 스케줄 상의 위치를 입력 받아 서버에 요청
                final Call<RouteData> routeDataCall = AppController.getRouteInfo()
                        .getTransportInfo(data.getCurrentLongitude(), data.getCurrentLatitude(), data.getLongitude(), data.getLatitude());

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
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
        }.executeOnExecutor(THREAD_POOL_EXECUTOR);
    }
}
