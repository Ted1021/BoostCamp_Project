package com.tedkim.smartschedule.model;

import io.realm.RealmObject;

/**
 * Created by tedkim on 2017. 8. 14..
 */

public class TrafficInfo extends RealmObject {

    int totalTime;  // 소요 시간
    int payment; // 총 요금
    int totalStationCount; // 총 정류장 합

}
