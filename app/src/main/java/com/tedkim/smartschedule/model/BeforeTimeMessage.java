package com.tedkim.smartschedule.model;

/**
 * Created by tedkim on 2017. 8. 24..
 */

public class BeforeTimeMessage {

    private String id;
    private int position;
    private int beforeTime;

    public BeforeTimeMessage(String id, int position, int beforeTime){

        this.id = id;
        this.position = position;
        this.beforeTime = beforeTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getBeforeTime() {
        return beforeTime;
    }

    public void setBeforeTime(int beforeTime) {
        this.beforeTime = beforeTime;
    }
}
