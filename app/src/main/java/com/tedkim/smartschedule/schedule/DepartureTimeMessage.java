package com.tedkim.smartschedule.schedule;

import java.util.Date;

/**
 * Created by tedkim on 2017. 8. 27..
 */

public class DepartureTimeMessage {

    String id;
    Date departTime;

    public DepartureTimeMessage(String id){

        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDepartTime() {
        return departTime;
    }

    public void setDepartTime(Date departTime) {
        this.departTime = departTime;
    }
}
