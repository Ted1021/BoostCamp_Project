package com.tedkim.smartschedule.model;

import java.util.Date;

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
    private Date startTime, endTime;

    private String address;

    private double latitude, longitude;
    private double currentLatitude=0, currentLongitude=0;

    //    private ContactsContract.Contacts contacts;

    // TODO - 다중 알람기능 추가를 위한 data type 구상필요...
    // 아니 이걸 이렇게 써야 하다니 ... 아직 한참 멀었구만 ...
    private RealmList<ReminderData> reminderList;

    private boolean alldaySchedule;
    private boolean fakeCall;

    public RealmList<RouteInfo> routeInfoList;

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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    //
//    public ContactsContract.Contacts getContacts() {
//        return contacts;
//    }
//
//    public void setContacts(ContactsContract.Contacts contacts) {
//        this.contacts = contacts;
//    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
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
