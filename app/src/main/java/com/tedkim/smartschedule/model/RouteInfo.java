package com.tedkim.smartschedule.model;

import java.util.Date;

/**
 * Created by tedkim on 2017. 8. 16..
 */

public class RouteInfo {

    Date departTime, arriveTime;
    int totalTime;

    // RealmList<RouteSeqData> routeSequence;
    int busStationCount, subwayStationCount;

    int payment;
}
