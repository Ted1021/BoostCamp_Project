package com.tedkim.smartschedule.service.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.tedkim.smartschedule.model.ReminderData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.DateConvertUtil;

import java.util.Calendar;
import java.util.UUID;

import io.realm.Realm;

/**
 * Created by tedkim on 2017. 8. 27..
 */

public class AlarmService {

    public static void setAlarm(Context context, String id){

        Realm realm = Realm.getDefaultInstance();

        UUID uuid = UUID.fromString(id);
        int alarmCode = uuid.hashCode();

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();

        ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", id).findFirst();
        for(ReminderData reminder : data.reminderList){
            if(reminder.isChecked()){
                calendar.setTimeInMillis(DateConvertUtil.calDateMin(data.getExpectedDepartTime(), reminder.getTime()).getTime());
                break;
            }
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);

        realm.close();
    }

    public static void removeAlarm(Context context, String id){

        Realm realm = Realm.getDefaultInstance();

        UUID uuid = UUID.fromString(id);
        int alarmCode = uuid.hashCode();

        Intent intent = new Intent();
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

        realm.close();
    }
}
