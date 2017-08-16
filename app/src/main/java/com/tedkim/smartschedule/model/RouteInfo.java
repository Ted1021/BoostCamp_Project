package com.tedkim.smartschedule.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by tedkim on 2017. 8. 16..
 */

public class RouteInfo extends RealmObject {

    private Date departTime, arriveTime;
    private int totalTime;

    private RealmList<RouteSeqData> routeSequence;
    private int busStationCount, subwayStationCount;

    private int payment;
}
