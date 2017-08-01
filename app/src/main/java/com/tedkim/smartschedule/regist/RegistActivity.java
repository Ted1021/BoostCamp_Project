package com.tedkim.smartschedule.regist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        Log.d("CHECK_ENTER","------------------- Regist Activity");

        // init Realm database;
        mRealm = Realm.getDefaultInstance();

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

                ScheduleData check = mRealm.where(ScheduleData.class).equalTo("title","Ted").findFirst();
                Toast.makeText(RegistActivity.this, R.string.database_transaction_success, Toast.LENGTH_SHORT).show();
                Log.d("CHECK_TRANSACTION", ">>>>>>>>>>>>>>"+check.getDesc());

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
}
