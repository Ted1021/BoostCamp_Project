package com.tedkim.smartschedule.model;

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

//    @PrimaryKey
//    private long _id;

    private String title;
    private String desc;

    //TODO - googleMap 의 전용 객체이므로 android 내장 객체를 이용해 주는 것이 좋아보임, Location 객체 학습해 볼 것.
//    private String location;
//    private ContactsContract.Contacts contacts;

    private boolean isAlldaySchedule;
    private String startTime, endTime;

//    private List<Integer> departureAlarmList;
    private boolean isCallAlarm;

//    public long get_id() {
//        return _id;
//    }
//
//    public void set_id(long _id) {
//        this._id = _id;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public ContactsContract.Contacts getContacts() {
//        return contacts;
//    }
//
//    public void setContacts(ContactsContract.Contacts contacts) {
//        this.contacts = contacts;
//    }

    public boolean isAlldaySchedule() {
        return isAlldaySchedule;
    }

    public void setAlldaySchedule(boolean alldaySchedule) {
        isAlldaySchedule = alldaySchedule;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

//    public List<Integer> getDepartureAlarmList() {
//        return departureAlarmList;
//    }
//
//    public void setDepartureAlarmList(List<Integer> departureAlarmList) {
//        this.departureAlarmList = departureAlarmList;
//    }

    public boolean isCallAlarm() {
        return isCallAlarm;
    }

    public void setCallAlarm(boolean callAlarm) {
        isCallAlarm = callAlarm;
    }
}
