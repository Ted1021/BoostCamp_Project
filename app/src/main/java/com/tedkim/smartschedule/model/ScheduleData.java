package com.tedkim.smartschedule.model;

import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.RealmObject;

/**
 * @file ScheduleData.java
 * @brief Managing ScheduleData with Realm
 *
 * @author 김태원
 * @date 2017.08.01
 *
 */

public class ScheduleData extends RealmObject {

    private String title;
    private String desc;

    //TODO - googleMap 의 전용 객체이므로 android 내장 객체를 이용해 주는 것이 좋아보임, Location 객체 학습해 볼 것.
    private LatLng location;
    private ContactsContract.Contacts contacts;

    private boolean isAlldaySchedule;
    private Calendar startTime, endTime;

    private ArrayList<Integer> departureAlarmList;
    private boolean isCallAlarm;

}
