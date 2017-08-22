package com.tedkim.smartschedule.model;

import java.util.Date;

import io.realm.RealmList;

/**
 * Created by tedkim on 2017. 8. 22..
 */

public class NotificationData {

    private String _id;
    private Date departTime;
    private RealmList<ReminderData> reminderSet;
}
