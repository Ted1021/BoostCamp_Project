package com.tedkim.smartschedule.detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ScheduleData;

/**
 * @file DetailActivity.java
 * @brief Show detail of selected schedule
 *
 * @author 김태원
 * @date 2017.07.31
 *
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    Toolbar mToolbar;

    ImageButton mBack, mCorrect, mDelete;
    TextView mTitle, mDate, mStart, mEnd, mAddress;

    ScheduleData mData;

    // TODO - 참여자, 알림 표시 방법 생각 할 것

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Log.d("CHECK_ENTER","------------------- Detail Activity");

        initView();
    }

    private void initView(){

        mToolbar = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0,0);

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mCorrect = (ImageButton) findViewById(R.id.imageButton_correct);
        mCorrect.setOnClickListener(this);

        mDelete = (ImageButton) findViewById(R.id.imageButton_delete);
        mDelete.setOnClickListener(this);

        mTitle = (TextView) findViewById(R.id.textView_title);
        mDate = (TextView) findViewById(R.id.textView_date);
        mStart = (TextView) findViewById(R.id.textView_start);
        mEnd = (TextView) findViewById(R.id.textView_end);
        mAddress = (TextView) findViewById(R.id.textView_address);
    }

    private void setData(){


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.imageButton_back:
                finish();
                break;

            case R.id.imageButton_correct:

                break;

            case R.id.imageButton_delete:

                break;

        }
    }
}
