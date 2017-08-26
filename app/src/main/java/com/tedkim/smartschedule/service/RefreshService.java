package com.tedkim.smartschedule.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

public class RefreshService extends Service {

    RefreshThread mRefreshThread;
    RefreshServiceHandler mRefreshServiceHandler;

    public RefreshService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mRefreshThread.stopForever();
        mRefreshThread = null;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mRefreshServiceHandler = new RefreshServiceHandler();
        mRefreshThread = new RefreshThread(mRefreshServiceHandler);
        mRefreshThread.start();
        return START_STICKY;

    }

    class RefreshServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {

            if(msg.what == 0){
                EventBus.getDefault().post(new RefreshMessage());
                Log.d("CHECK_REFRESH_SERVICE", "refresh service >>> 새로고침 수행");
                Toast.makeText(RefreshService.this, "refresh 동작 체크", Toast.LENGTH_LONG).show();
            }
        }
    }
}
