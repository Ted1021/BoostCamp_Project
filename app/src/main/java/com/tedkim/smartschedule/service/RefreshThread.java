package com.tedkim.smartschedule.service;

import android.os.Handler;

/**
 * Created by tedkim on 2017. 8. 26..
 */

public class RefreshThread extends Thread{

    Handler mHandler;
    boolean isRun = true;
    private static final int REFRESH_INTERVAL =  10000;   //1000 * 60 * 10;
    private static final int REQ_REFRESH_SCHEDULE = 0;

    public RefreshThread(Handler handler){
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
            mHandler.sendEmptyMessage(REQ_REFRESH_SCHEDULE);//쓰레드에 있는 핸들러에게 메세지를 보냄
            try{
                Thread.sleep(REFRESH_INTERVAL); //10분씩 체크한다
            }catch (Exception e) {}
        }
    }
}
