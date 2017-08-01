package com.tedkim.smartschedule.util;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by tedkim on 2017. 8. 1..
 */

public class RealmDatabaseInit extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
    }
}
