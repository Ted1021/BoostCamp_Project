package com.tedkim.smartschedule.model;

import io.realm.RealmObject;

/**
 * Created by tedkim on 2017. 8. 9..
 */

public class ReminderData extends RealmObject {

    private String _id;
    private int time;
    private boolean checked;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

