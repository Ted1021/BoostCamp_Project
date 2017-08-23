package com.tedkim.smartschedule.schedule;

import com.tedkim.smartschedule.model.RouteInfo;

/**
 * Created by tedkim on 2017. 8. 23..
 */

public interface OnTrafficInfoListener {

    void onTrafficInfoClickListener(ScheduleRouteListAdapter.ViewHolder viewHolder, RouteInfo routeInfo);
}
