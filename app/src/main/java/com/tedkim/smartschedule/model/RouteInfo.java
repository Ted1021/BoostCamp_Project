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
    private Double totalDistance;
    private int totalTime;
    private int payment;
    private int busTransitCount, subwayTransitCount, totalTransitCount;
    private boolean selected;

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

    public int getBusTransitCount() {
        return busTransitCount;
    }

    public void setBusTransitCount(int busTransitCount) {
        this.busTransitCount = busTransitCount;
    }

    public int getSubwayTransitCount() {
        return subwayTransitCount;
    }

    public void setSubwayTransitCount(int subwayTransitCount) {
        this.subwayTransitCount = subwayTransitCount;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public RealmList<RouteSeqData> getRouteSequence() {
        return routeSequence;
    }

    public void setRouteSequence(RealmList<RouteSeqData> routeSequence) {
        this.routeSequence = routeSequence;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public int getTotalTransitCount() {
        return totalTransitCount;
    }

    public void setTotalTransitCount(int totalTransitCount) {
        this.totalTransitCount = totalTransitCount;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
