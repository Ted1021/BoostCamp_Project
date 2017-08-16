package com.tedkim.smartschedule.regist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.tedkim.smartschedule.R;

public class ReminderActivity extends AppCompatActivity implements View.OnClickListener {

    Toolbar mToolbar;
    ImageButton mBack, mSave;
    RadioButton mOnTime, mTenMin, mThirtyMin, mOneHour, mOneDay;
    RadioGroup mRadioGroup;

    String mTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        initView();
    }

    private void initView(){

        mToolbar = (Toolbar) findViewById(R.id.toolbar_reminder);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0, 0);

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_moreInfo);
        mSave.setOnClickListener(this);

        mOnTime = (RadioButton) findViewById(R.id.radioButton_onTime);
        mOnTime.setOnClickListener(this);

        mTenMin = (RadioButton) findViewById(R.id.radioButton_10min);
        mTenMin.setOnClickListener(this);

        mThirtyMin = (RadioButton) findViewById(R.id.radioButton_30min);
        mThirtyMin.setOnClickListener(this);

        mOneHour = (RadioButton) findViewById(R.id.radioButton_1hour);
        mOneHour.setOnClickListener(this);

        mOneDay = (RadioButton) findViewById(R.id.radioButton_1day);
        mOneDay.setOnClickListener(this);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
    }

    private void setReminder(){

        Intent intent = getIntent();
        setResult(RESULT_OK, intent.putExtra("REMINDER", mTime));
        finish();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.imageButton_back:
                finish();
                break;

            case R.id.imageButton_moreInfo:
                if(mRadioGroup.getCheckedRadioButtonId() != -1){
                    setReminder();
                }
                break;

            case R.id.radioButton_onTime:
                mTime = "정시";
                break;

            case R.id.radioButton_10min:
                mTime = "10분전";
                break;

            case R.id.radioButton_30min:
                mTime = "30분전";
                break;

            case R.id.radioButton_1hour:
                mTime = "1시간전";
                break;

            case R.id.radioButton_1day:
                mTime = "1일전";
                break;
        }
    }
}
