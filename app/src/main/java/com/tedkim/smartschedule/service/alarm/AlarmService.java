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

    public static void setAlarm(Context context, String id) {

        Realm realm = Realm.getDefaultInstance();

        UUID uuid = UUID.fromString(id);
        int alarmCode = uuid.hashCode();

        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent pendingIntent;
        Calendar calendar = Calendar.getInstance();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", id).findFirst();
        for (ReminderData reminder : data.reminderList) {
            if (reminder.isChecked()) {

                long targetTime = DateConvertUtil.calDateMin(data.getExpectedDepartTime(), reminder.getTime()).getTime();

                intent.putExtra("ID", data.get_id());
                intent.putExtra("TITLE", data.getTitle());
                intent.putExtra("MIN", reminder.getTime());

                pendingIntent = PendingIntent.getBroadcast(context, alarmCode+reminder.getTime(), intent, PendingIntent.FLAG_ONE_SHOT);

                if(targetTime - System.currentTimeMillis() < 0){
                    alarmManager.cancel(pendingIntent);
                }else{
                    calendar.setTimeInMillis(DateConvertUtil.calDateMin(data.getExpectedDepartTime(), reminder.getTime()).getTime());
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                break;
            }
        }
        realm.close();
    }

    public static void removeAlarm(Context context, String id) {

        Realm realm = Realm.getDefaultInstance();

        UUID uuid = UUID.fromString(id);
        int alarmCode = uuid.hashCode();

        Intent intent = new Intent();
        PendingIntent pendingIntent;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        ScheduleData data = realm.where(ScheduleData.class).equalTo("_id", id).findFirst();
        for (ReminderData reminder : data.reminderList) {
            if (reminder.isChecked()) {

                pendingIntent = PendingIntent.getBroadcast(context, alarmCode+reminder.getTime(), intent, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.cancel(pendingIntent);
            }
        }
        realm.close();
    }
}
