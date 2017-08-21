package com.tedkim.smartschedule.model;

import io.realm.RealmObject;

/**
 * Created by tedkim on 2017. 8. 16..
 */

public class RouteSeqData extends RealmObject {

    private String _id;
    private int trafficType;

    private String subwayName;  // 노선 이름
    private int subwayType;  // 노선 코드

    private String busName; // 버스 명
    private int busType; // 버스 종류

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSubwayName() {
        return subwayName;
    }

    public void setSubwayName(String subwayName) {
        this.subwayName = subwayName;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public int getTrafficType() {
        return trafficType;
    }

    public void setTrafficType(int trafficType) {
        this.trafficType = trafficType;
    }

    public int getSubwayType() {
        return subwayType;
    }

    public void setSubwayType(int subwayType) {
        this.subwayType = subwayType;
    }

    public int getBusType() {
        return busType;
    }

    public void setBusType(int busType) {
        this.busType = busType;
    }
}
