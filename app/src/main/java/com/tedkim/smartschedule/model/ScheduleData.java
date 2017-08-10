package com.tedkim.smartschedule.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @file ScheduleData.java
 * @brief Managing ScheduleData with Realm
 *
 * @author 김태원
 * @date 2017.08.01
 *
 */

public class ScheduleData extends RealmObject {

    @PrimaryKey
    private long _id;

    private String title;
    private String desc;

    private String date;
    private String startTime, endTime;

    //TODO - googleMap 의 전용 객체이므로 android 내장 객체를 이용해 주는 것이 좋아보임, Location 객체 학습해 볼 것.
    private String address;

    //    private ContactsContract.Contacts contacts;

    // TODO - 다중 알람기능 추가를 위한 data type 구상필요...
    // 아니 이걸 이렇게 써야 하다니 ... 아직 한참 멀었구만 ...
    private RealmList<ReminderData> reminderList;

    private boolean alldaySchedule;
    private boolean fakeCall;


    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
//
//    public ContactsContract.Contacts getContacts() {
//        return contacts;
//    }
//
//    public void setContacts(ContactsContract.Contacts contacts) {
//        this.contacts = contacts;
//    }

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

    public RealmList<ReminderData> getReminderList() {
        return reminderList;
    }

    public void setReminderList(RealmList<ReminderData> reminderList) {
        this.reminderList = reminderList;
    }

    public boolean isAlldaySchedule() {
        return alldaySchedule;
    }

    public void setAlldaySchedule(boolean alldaySchedule) {
        this.alldaySchedule = alldaySchedule;
    }

    public boolean isFakeCall() {
        return fakeCall;
    }

    public void setFakeCall(boolean fakeCall) {
        this.fakeCall = fakeCall;
    }
}
