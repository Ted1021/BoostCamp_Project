package com.tedkim.smartschedule.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.home.HomeActivity;
import com.tedkim.smartschedule.service.RefreshService;

/**
 * @author 김태원
 * @file SplashActivity.java
 * @brief Get permissions (calendar, address, location) for app service
 * @date 2017.07.31
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPALSH_DISPLAY_LENGTH = 2000;
    private final int REQ_LOCATION_PERMISSION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 로고 이미지 로딩
        ImageView ivLogo = (ImageView) findViewById(R.id.imageView_logo);
        Glide.with(SplashActivity.this).load(R.drawable.smart_schedule).into(ivLogo);

        // App 구동에 필요한 서비스 시작 (Notification, 이동정보 Refresh)
        startService();

        // Home Activity 로 분기
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkPermission();
            }
        }, SPALSH_DISPLAY_LENGTH);
    }

    private void startService(){

        // Notification service
//        Log.d("CHECK_SERVICE", "Splash Activity >>> Notification service start !! ");
//        Intent notificationIntent = new Intent(SplashActivity.this, NotificationService.class);
//        startService(notificationIntent);

        // Refresh service
        Log.d("CHECK_SERVICE", "Splash Activity >>> refresh service start !! ");
        Intent refreshIntent = new Intent(SplashActivity.this, RefreshService.class);
        startService(refreshIntent);
    }

    private void checkPermission() {

        Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION));

        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMISSION);
        } else {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }

    // permission 요청 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 요청한 permission 들에 대해...
        if (requestCode == REQ_LOCATION_PERMISSION) {

            for (int grant : grantResults) {

                Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION));
                Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION));

                if (grant == PackageManager.PERMISSION_DENIED) {

                    finish();
                    return;
                }
            }

            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
        }
    }
}
