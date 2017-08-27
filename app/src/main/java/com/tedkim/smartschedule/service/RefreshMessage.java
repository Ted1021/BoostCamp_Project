package com.tedkim.smartschedule.service;

/**
 * Created by tedkim on 2017. 8. 26..
 */

public class RefreshMessage {

    private int req;

    public RefreshMessage(int req){
        this.req = req;
    }

    public int getReq() {
        return req;
    }

    public void setReq(int req) {
        this.req = req;
    }
}
