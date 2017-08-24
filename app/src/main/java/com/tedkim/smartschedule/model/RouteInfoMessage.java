package com.tedkim.smartschedule.model;

/**
 * Created by tedkim on 2017. 8. 24..
 */

public class RouteInfoMessage {

    private String id;
    private int totalTime;
    private Double distance;

    public RouteInfoMessage(String id, int totalTime, Double distance){
        this.id = id;
        this.totalTime = totalTime;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
