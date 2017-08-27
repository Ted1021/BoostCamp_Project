package com.tedkim.smartschedule.service.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.home.HomeActivity;

/**
 * Created by tedkim on 2017. 8. 27..
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {

        //NotificationManager 안드로이드 상태바에 메세지를 던지기위한 서비스 불러오고
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.drawable.smart_schedule)
                .setTicker("SmartSchedule")
                .setWhen(System.currentTimeMillis())
                .setContentTitle("출발 30분 전입니다")
                .setContentText("부스트캠프")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());
    }
}
