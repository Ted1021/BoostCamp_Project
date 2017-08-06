package com.tedkim.smartschedule.regist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ScheduleData;

import io.realm.Realm;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener {

    // ui components
    Toolbar mToolbar;
    ImageButton mBack, mSave;
    EditText mTitle, mDesc, mAddress, mContacts;
    TextView mCalendarDate, mStart, mEnd, mNotification;
    CheckBox mAllDay, mFakeCall;

    // realm database instance
    private Realm mRealm;

    // date info from Home Activity
    private String mDate;

    @Override
    protected void onStart() {
        super.onStart();

        // init Realm database
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // close Realm database
        mRealm.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        mDate = getIntent().getStringExtra("DATE");

        Log.d("CHECK_ENTER","Register Activity -------------------");
        Log.d("CHECK_DATE", "In register >>>>>>>>>>>>>>>>"+mDate);

        initView();
    }

    public void initView(){

        mToolbar = (Toolbar) findViewById(R.id.toolbar_regist);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0,0);

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_save);
        mSave.setOnClickListener(this);

        mTitle = (EditText) findViewById(R.id.editText_title);
        mDesc = (EditText) findViewById(R.id.editText_desc);

        mCalendarDate = (TextView) findViewById(R.id.textView_date);
        mCalendarDate.setText(mDate);
        mCalendarDate.setOnClickListener(this);

        mStart = (TextView) findViewById(R.id.textView_start);
        mStart.setOnClickListener(this);

        mEnd = (TextView) findViewById(R.id.textView_end);
        mEnd.setOnClickListener(this);

        mAddress = (EditText) findViewById(R.id.editText_address);
        mContacts = (EditText) findViewById(R.id.editText_contacts);

        mNotification = (TextView) findViewById(R.id.textView_notification);
        mNotification.setOnClickListener(this);

        mAllDay = (CheckBox) findViewById(R.id.checkBox_allDay);
        mFakeCall = (CheckBox) findViewById(R.id.checkBox_fakeCall);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.imageButton_back:
                finish();
                break;

            case R.id.imageButton_save:
                insertSchedule();
                finish();
                break;

            case R.id.textView_date:

                break;

            case R.id.textView_start:

                break;

            case R.id.textView_end:

                break;

            case R.id.textView_notification:

                break;
        }
    }

    private void insertSchedule(){

//        if(isEmptyEditors(mTitle) || isEmptyEditors(mDesc) || isEmptyEditors(mLocation) || isEmptyEditors(mStart) || isEmptyEditors(mEnd)){
//
//            Toast.makeText(this, "모든 항목을 채워 주셔야 합니다", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Async database transaction
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ScheduleData newSchedule = mRealm.createObject(ScheduleData.class,getNextKey());

                newSchedule.setDate(mDate);
                newSchedule.setTitle(mTitle.getText().toString());
                newSchedule.setDesc(mDesc.getText().toString());
//                newSchedule.setLocation();
//                newSchedule.setContacts();

                newSchedule.setStartTime(mStart.getText().toString());
                newSchedule.setEndTime(mEnd.getText().toString());

                newSchedule.setAddress(mAddress.getText().toString());

                if (mAllDay.isActivated()) {
                    newSchedule.setAlldaySchedule(true);
                } else {
                    newSchedule.setAlldaySchedule(false);
                }

                if (mFakeCall.isActivated()) {
                    newSchedule.setCallAlarm(true);
                } else {
                    newSchedule.setCallAlarm(false);
                }
            }
        });
    }


    // check valid about editTexts
    private boolean isEmptyEditors(EditText view) {
        if(TextUtils.isEmpty(view.getText().toString())) {
            view.setError("잘좀 써라");
            view.requestFocus();
            return true;
        }
        return false;
    }

    //
    public int getNextKey() {
        try {
            Number number = mRealm.where(ScheduleData.class).max("_id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
}
