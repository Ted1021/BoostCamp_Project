package com.tedkim.smartschedule.service;

import android.os.Handler;

/**
 * Created by tedkim on 2017. 8. 26..
 */

public class NotificationThread extends Thread {

    Handler mHandler;
    boolean isRun = true;

    public NotificationThread(Handler handler){
        this.mHandler = handler;
    }

    public void stopForever(){
        synchronized (this) {
            this.isRun = false;
        }
    }

    public void run(){
        //반복적으로 수행할 작업을 한다.
        while(isRun){
            mHandler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
            try{
                Thread.sleep(10000); //10초씩 쉰다.
            }catch (Exception e) {}
        }
    }
}
