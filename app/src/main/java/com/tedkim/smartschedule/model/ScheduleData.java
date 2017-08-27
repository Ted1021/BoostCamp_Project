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
    private String _id;
    private String title;
    private String memo;
    private String date;
    private Date startTime, endTime;
    private String address;
    private double latitude, longitude;
    private double currentLatitude, currentLongitude;
    //    private ContactsContract.Contacts contacts;
    public RealmList<ReminderData> reminderList;
    private boolean allDaySchedule;
    private boolean fakeCall;
    private int busCount, subwayCount, subwayBusCount;

    private Date expectedDepartTime;
    private int totalTime;
    private Double distance;
    private int beforeTime;

    public RealmList<RouteInfo> routeInfoList;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public boolean isAllDaySchedule() {
        return allDaySchedule;
    }

    public void setAllDaySchedule(boolean allDaySchedule) {
        this.allDaySchedule = allDaySchedule;
    }

    public boolean isFakeCall() {
        return fakeCall;
    }

    public void setFakeCall(boolean fakeCall) {
        this.fakeCall = fakeCall;
    }

    public int getBusCount() {
        return busCount;
    }

    public void setBusCount(int busCount) {
        this.busCount = busCount;
    }

    public int getSubwayCount() {
        return subwayCount;
    }

    public void setSubwayCount(int subwayCount) {
        this.subwayCount = subwayCount;
    }

    public int getSubwayBusCount() {
        return subwayBusCount;
    }

    public void setSubwayBusCount(int subwayBusCount) {
        this.subwayBusCount = subwayBusCount;
    }

    public int getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(int beforeTime) {
        this.beforeTime = beforeTime;
    }

    public Date getExpectedDepartTime() {
        return expectedDepartTime;
    }

    public void setExpectedDepartTime(Date expectedDepartTime) {
        this.expectedDepartTime = expectedDepartTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
