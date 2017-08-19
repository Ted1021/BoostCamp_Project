package com.tedkim.smartschedule.model;

import io.realm.RealmObject;

/**
 * Created by tedkim on 2017. 8. 9..
 */

public class ReminderData extends RealmObject {

    private String _id;
    private int reminder;

    public int getReminder() {
        return reminder;
    }

    public void setReminder(int reminder) {
        this.reminder = reminder;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
