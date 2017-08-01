package com.tedkim.smartschedule.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.home.HomeActivity;

/**
 * @file SplashActivity.java
 * @brief Get permissions (calendar, address, location) for app service
 *
 * @author 김태원
 * @date 2017.07.31
 *
 */

public class SplashActivity extends AppCompatActivity {

    private final int SPALSH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 로고 이미지 로딩
        ImageView ivLogo = (ImageView) findViewById(R.id.imageView_logo);
        Glide.with(SplashActivity.this).load(R.drawable.smart_schedule).into(ivLogo);

        // TODO - Permission 관련 로직 작성

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

}
