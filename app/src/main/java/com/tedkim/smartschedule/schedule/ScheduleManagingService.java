package com.tedkim.smartschedule.schedule;

import com.tedkim.smartschedule.model.Message;
import com.tedkim.smartschedule.model.RouteData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @file ScheduleManagingService.java
 * @brief Request transport information for schedule
 *
 * @author 김태원
 * @date 2017.08.13
 */
public interface ScheduleManagingService {

    @GET("/route")
    Call<RouteData> getTransportInfo(@Query("SX") double SX, @Query("SY") double SY,
                                     @Query("EX") double EX, @Query("EY") double EY);

    @GET("/message")
    Call<Message> getMessage();
}
