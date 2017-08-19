package com.tedkim.smartschedule.regist;

import java.util.HashMap;

/**
 * Created by tedkim on 2017. 8. 19..
 */

public interface OnReminderListSaveListener {

    void onReminderListChangedListener(HashMap<Integer, Boolean> notificationList);
}
