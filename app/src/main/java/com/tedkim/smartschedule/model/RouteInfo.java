package com.tedkim.smartschedule.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by tedkim on 2017. 8. 16..
 */

public class RouteInfo extends RealmObject {

    private String _id;
    private Date departTime, arriveTime;
    public RealmList<RouteSeqData> routeSequence;
    private int totalTime;
    private int payment;
    private int busStationCount, subwayStationCount;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Date departTime) {
        this.departTime = departTime;
    }

    public Date getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Date arriveTime) {
        this.arriveTime = arriveTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getBusStationCount() {
        return busStationCount;
    }

    public void setBusStationCount(int busStationCount) {
        this.busStationCount = busStationCount;
    }

    public int getSubwayStationCount() {
        return subwayStationCount;
    }

    public void setSubwayStationCount(int subwayStationCount) {
        this.subwayStationCount = subwayStationCount;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }
}
