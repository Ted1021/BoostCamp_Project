package com.tedkim.smartschedule.service.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.home.HomeActivity;
import com.tedkim.smartschedule.model.ScheduleData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import io.realm.Realm;

public class NotificationService extends Service {

    NotificationManager mNotificationManager;
    Notification mNotification;

    public NotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Date expectedDepartTime = DateConvertUtil.calDateMin(data.getStartTime(), data.getBeforeTime() + data.getTotalTime());
//        long interval = expectedDepartTime.getTime() - System.currentTimeMillis();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return START_STICKY;
    }

    // From ScheduleFragment (line 173)
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onAlertNotification(final NotificationMessage event) {

        final String id = event.getId();

        new Thread(){
            @Override
            public void run() {
                super.run();

                Intent intent = new Intent(NotificationService.this, HomeActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Realm realm = Realm.getDefaultInstance();
                ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", id).findFirst();

                mNotification = new Notification.Builder(getApplicationContext())
                        .setContentTitle("지금 당장 출발하셔야 합니다!")
                        .setContentText(data.getTitle())
                        .setSmallIcon(R.drawable.smart_schedule)
                        .setContentIntent(pendingIntent)
                        .build();

                //소리추가
                mNotification.defaults = Notification.DEFAULT_SOUND;
                //알림 소리를 한번만 내도록
                mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
                //확인하면 자동으로 알림이 제거 되도록
                mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                mNotificationManager.notify(777, mNotification);

                realm.close();
            }
        }.start();
    }
}
