package com.tedkim.smartschedule.detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tedkim.smartschedule.R;

/**
 * @file DetailActivity.java
 * @brief Show detail of single schedule
 *
 * @author 김태원
 * @date 2017.07.31
 *
 */

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.d("CHECK_ENTER","------------------- Detail Activity");

    }
}
