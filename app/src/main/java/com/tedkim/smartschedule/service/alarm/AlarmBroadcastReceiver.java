package com.tedkim.smartschedule.service.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.home.HomeActivity;

import java.util.UUID;

/**
 * Created by tedkim on 2017. 8. 27..
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver{

    NotificationManager mNotificationManager;
    Notification mNotification;

    @Override
    public void onReceive(Context context, Intent intent) {

        String id = intent.getStringExtra("ID");
        String title = intent.getStringExtra("TITLE");
        int min = intent.getIntExtra("MIN",0);

        UUID uuid = UUID.fromString(id);
        int alarmCode = uuid.hashCode();

        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, alarmCode+min, new Intent(context, HomeActivity.class), PendingIntent.FLAG_ONE_SHOT);

        mNotification = new Notification.Builder(context)
                .setTicker("SmartSchedule")
                .setSmallIcon(R.drawable.smart_schedule)
                .setContentTitle(String.format("출발 %d분 전입니다.",min))
                .setContentText(title)
                .setContentIntent(pendingIntent)
                .build();

        //소리추가
        mNotification.defaults = Notification.DEFAULT_SOUND;
        //알림 소리를 한번만 내도록
        mNotification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        //확인하면 자동으로 알림이 제거 되도록
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(alarmCode+min, mNotification);
    }
}
