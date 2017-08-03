package com.tedkim.smartschedule.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 김태원
 * @file DateConverter.java
 * @brief String to Date / Date to String converter class
 * @date 2017.08.03
 */

public class DateConverter {

    public static Date string2Date(String string){

        try {
            return new SimpleDateFormat("yyyy-M-d").parse(string);

        } catch (ParseException e) {

            e.printStackTrace();
            Log.e("ERROR__DATE_CONVERT", "String to Date >>>>>>>>>>>");

        }finally{

            return null;
        }
    }

    public static String date2String(Date date){

        try{
            return new SimpleDateFormat("yyyy-M-d").format(date);

        }catch(Exception e){

            e.printStackTrace();
            Log.e("ERROR__DATE_CONVERT", "Date to String >>>>>>>>>>>");

        }
        finally{

            return null;
        }
    }
}
