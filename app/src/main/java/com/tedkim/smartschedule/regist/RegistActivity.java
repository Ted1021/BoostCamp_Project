package com.tedkim.smartschedule.regist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ScheduleData;

import io.realm.Realm;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener {

    // ui components
    private Toolbar mToolbar;
    private ImageButton mBack, mSave, mSearchLocation, mSearchContact;
    private EditText mTitle, mDesc, mLocation, mStart, mEnd;
    private Switch mAllDay, mCallAlarm;

    // realm database instance
    private Realm mRealm;

    private String mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        Log.d("CHECK_ENTER","Register Activity -------------------");

        // init Realm database;
        mRealm = Realm.getDefaultInstance();

        // init date
        String date = getIntent().getStringExtra("DATE");
        Log.d("CHECK_DATE", "In register >>>>>>>>>>>>>>>>"+date);

        initView();
    }

    public void initView(){

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0,0);

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_save);
        mSave.setOnClickListener(this);

        mSearchLocation = (ImageButton) findViewById(R.id.imageButton_location);
        mSearchLocation.setOnClickListener(this);

        mSearchContact = (ImageButton) findViewById(R.id.imageButton_contact);
        mSearchContact.setOnClickListener(this);

        mTitle = (EditText) findViewById(R.id.editText_title);
        mDesc = (EditText) findViewById(R.id.editText_desc);
        mLocation = (EditText) findViewById(R.id.editText_location);
        mStart = (EditText) findViewById(R.id.editText_start);
        mEnd = (EditText) findViewById(R.id.editText_end);

        mAllDay = (Switch) findViewById(R.id.switch_allDay);
        mCallAlarm = (Switch) findViewById(R.id.switch_callAlarm);

    }

    private void addSchedule(){

        if(isEmptyEditors(mTitle) || isEmptyEditors(mDesc) || isEmptyEditors(mLocation) || isEmptyEditors(mStart) || isEmptyEditors(mEnd)){

            Toast.makeText(this, "모든 항목을 채워 주셔야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // Async database transaction
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ScheduleData newSchedule = mRealm.createObject(ScheduleData.class);

                newSchedule.setTitle(mTitle.getText().toString());
                newSchedule.setDesc(mDesc.getText().toString());
//                newSchedule.setLocation();
//                newSchedule.setContacts();

                newSchedule.setStartTime(mStart.getText().toString());
                newSchedule.setEndTime(mEnd.getText().toString());

                if (mAllDay.isActivated()) {
                    newSchedule.setAlldaySchedule(true);
                } else {
                    newSchedule.setAlldaySchedule(false);
                }

                if (mCallAlarm.isActivated()) {
                    newSchedule.setCallAlarm(true);
                } else {
                    newSchedule.setCallAlarm(false);
                }
            }
            // transaction onSuccess method
        });
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.imageButton_back:

                finish();

                break;

            case R.id.imageButton_save:

                addSchedule();

                break;

            case R.id.imageButton_location:

                break;

            case R.id.imageButton_contact:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    // 빈칸 확인
    private boolean isEmptyEditors(EditText view) {
        if(TextUtils.isEmpty(view.getText().toString())) {
            view.setError("잘좀 써라");
            view.requestFocus();
            return true;
        }
        return false;
    }
}
