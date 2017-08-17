package com.tedkim.smartschedule.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tedkim on 2017. 8. 15..
 */

public class DateConvertUtil {

    private static SimpleDateFormat timeFormat = new SimpleDateFormat("a h시 mm분");
    private static SimpleDateFormat minFormat = new SimpleDateFormat("mm분");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
    private static SimpleDateFormat monthTitleFormat = new SimpleDateFormat("yyyy년 M월");
    private static SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-M");

    public static String time2string(Date date) {
        return timeFormat.format(date);
    }



    public static String date2string(Date date) {
        return dateFormat.format(date);
    }

    public static String month2string(Date date) {
        return monthTitleFormat.format(date);
    }

    public static String yearMonth2string(Date date) {
        return yearMonthFormat.format(date);
    }

    public static Date string2date(String target) {

        try {
            return dateFormat.parse(target);
        } catch (ParseException e) {
            Log.e("ERROR_CONVERT_DATE", "cannot convert string to date");
            e.printStackTrace();
        }
        return null;
    }

    public static Date string2time(String target) {
        return null;
    }

    public static Date calDateMin(Date targetDate, int targetMin){

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(targetDate);
        calendar.add(Calendar.MINUTE, -targetMin);

        return calendar.getTime();
    }

}
