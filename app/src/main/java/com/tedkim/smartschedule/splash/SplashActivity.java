package com.tedkim.smartschedule.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.home.HomeActivity;

/**
 * @author 김태원
 * @file SplashActivity.java
 * @brief Get permissions (calendar, address, location) for app service
 * @date 2017.07.31
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPALSH_DISPLAY_LENGTH = 2000;
    private final int REQ_LOCATION_PERMISSION = 100;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 로고 이미지 로딩
        ImageView ivLogo = (ImageView) findViewById(R.id.imageView_logo);
        Glide.with(SplashActivity.this).load(R.drawable.smart_schedule).into(ivLogo);

        // TODO - Permission 관련 로직 작성
        checkPermission();

        // Home Activity 로 분기
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, SPALSH_DISPLAY_LENGTH);
    }

    // TODO - min SDK version 을 23 으로 바꿀 것.
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermission() {

        Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION));
        Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION));

        if (ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMISSION);
        }
        else{

        }
    }

    // permission 요청 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // 요청한 permission 들에 대해...
        if (requestCode == REQ_LOCATION_PERMISSION) {

            // 1. 권한을 체크한다
            for (int grant : grantResults) {

                Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION));
                Log.d("CHECK_PERMISSION", ">>>>>>>>> in splash activity " + ActivityCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION));

                // 1.1. ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION 중 하나라도 거부되었다면 메세지 호출
                if (grant == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(SplashActivity.this.getWindow().getDecorView().getRootView(), R.string.error_message_location, Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }
}
