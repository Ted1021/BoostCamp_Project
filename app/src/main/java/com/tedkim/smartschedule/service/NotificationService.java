package com.tedkim.smartschedule.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.home.HomeActivity;

public class NotificationService extends Service {

    NotificationManager mNotificationManager;
    NotificationThread mNotificationThread;
    Notification mNotification;

    public NotificationService() {
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
        mNotificationThread.stopForever();
        mNotificationThread = null; //쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationServiceHandler handler = new NotificationServiceHandler();
        mNotificationThread = new NotificationThread(handler);
        mNotificationThread.start();
        return START_STICKY;
    }

    class NotificationServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            Intent intent = new Intent(NotificationService.this, HomeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(NotificationService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            mNotification = new Notification.Builder(getApplicationContext())
                    .setContentTitle("10분 안에는 출발하셔야 합니다")
                    .setContentText("강남역 불금 파티")
                    .setSmallIcon(R.drawable.smart_schedule)
                    .setContentIntent(pendingIntent)
                    .build();

            //소리추가
            mNotification.defaults = Notification.DEFAULT_SOUND;
            //알림 소리를 한번만 내도록
            mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            //확인하면 자동으로 알림이 제거 되도록
            mNotification.flags = Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify( 777 , mNotification);

            //토스트 띄우기
            Toast.makeText(NotificationService.this, "notification 동작 체크", Toast.LENGTH_LONG).show();
        }
    }
}
