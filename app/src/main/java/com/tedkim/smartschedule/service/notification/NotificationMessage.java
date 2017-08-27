package com.tedkim.smartschedule.service.notification;

/**
 * Created by tedkim on 2017. 8. 27..
 */

public class NotificationMessage {

    String id;

    public NotificationMessage(String id){

        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
