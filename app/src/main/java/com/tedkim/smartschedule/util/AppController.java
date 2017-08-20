package com.tedkim.smartschedule.util;

import android.app.Application;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.tedkim.smartschedule.schedule.ScheduleManagingService;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tedkim on 2017. 8. 1..
 */

public class AppController extends Application {

    private static String BASE_URL = "http://174.138.18.90/";
    private static Retrofit mRetrofit;
    private static ScheduleManagingService mService;

    public static int REQ_CORRECT = 100;
    public static int REQ_CREATE = 101;

    public static int REQ_REMINDER = 102;
    public static int REQ_GOOGLE_MAP = 103;
    public static int REQ_GOOGLE_PLACES = 104;

    @Override
    public void onCreate() {
        super.onCreate();

        // init Realm
        Realm.init(this);

        // init Retrofit service
        mRetrofit = new Retrofit.Builder()
                .client(new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new StethoInterceptor()).build())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ScheduleManagingService getRouteInfo() {

        mService = mRetrofit.create(ScheduleManagingService.class);
        return mService;
    }
}
