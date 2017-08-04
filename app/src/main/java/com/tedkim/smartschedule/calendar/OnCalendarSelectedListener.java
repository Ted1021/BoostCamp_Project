package com.tedkim.smartschedule.calendar;

import java.util.Date;

/**
 * @author 김태원
 * @file OnCalendarSelectedListener.java
 * @brief Transfer Selected Date in calendar to HomeActivity
 * @date 2017.08.02
 */

public interface OnCalendarSelectedListener {

    void onDateSelectedListener(Date date);
}
