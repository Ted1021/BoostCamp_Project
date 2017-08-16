package com.tedkim.smartschedule.model;

import io.realm.RealmObject;

/**
 * Created by tedkim on 2017. 8. 16..
 */

public class RouteSeqData extends RealmObject {

    private String subwayName;
    private String busName;
    private int trafficType;

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

}
