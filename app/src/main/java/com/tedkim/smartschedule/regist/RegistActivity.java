package com.tedkim.smartschedule.regist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ScheduleData;

import io.realm.Realm;

import static com.tedkim.smartschedule.home.HomeActivity.ACTION_CREATE;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener {

    // ui components
    Toolbar mToolbar;
    ImageButton mBack, mSave;
    EditText mTitle, mDesc, mAddress, mContacts;
    TextView mDate, mStart, mEnd, mNotification;
    CheckBox mAllDay, mFakeCall;

    // realm database instance
    Realm mRealm;

    // date info from Home Activity
    String mDateInfo, mStartInfo, mEndInfo;

    int mPosition;

    private static final int SET_START = 0;
    private static final int SET_END = 1;

    int mTimeset = SET_START;

    @Override
    protected void onStart() {
        super.onStart();

//        // init Realm database
//        mRealm = Realm.getDefaultInstance();
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

        // init Realm database
        mRealm = Realm.getDefaultInstance();

        mPosition = getIntent().getIntExtra("POSITION", ACTION_CREATE);
        mDateInfo = getIntent().getStringExtra("DATE");

        Log.d("CHECK_ENTER", "Register Activity -------------------");
        Log.d("CHECK_DATE", "In register >>>>>>>>>>>>>>>>" + mDateInfo);

        initView();

        if (mPosition != ACTION_CREATE) {
            setData();
        }
    }

    public void initView() {

        mToolbar = (Toolbar) findViewById(R.id.toolbar_regist);
        setSupportActionBar(mToolbar);
        mToolbar.setContentInsetsAbsolute(0, 0);

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_save);
        mSave.setOnClickListener(this);

        mTitle = (EditText) findViewById(R.id.editText_title);
        mDesc = (EditText) findViewById(R.id.editText_desc);

        mDate = (TextView) findViewById(R.id.textView_date);
        mDate.setText(mDateInfo);
        mDate.setOnClickListener(this);

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

    private void setData() {

        ScheduleData result = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();

        mTitle.setText(result.getTitle());
        mDesc.setText(result.getDesc());
        mStart.setText(result.getStartTime());
        mEnd.setText(result.getEndTime());
        mAddress.setText(result.getAddress());

        // TODO - 참여자, 리마인더 표현방법 구상 필요
//        mContacts.setText(result.getContacts());

        mAllDay.setChecked(result.isAlldaySchedule());
        mFakeCall.setChecked(result.isFakeCall());

    }

    @Override
    public void onClick(View v) {

        // TODO - 각각 현재 날짜와 시간 가져오는 방법을 알아 볼 것
        DatePickerDialog dateDialog = new DatePickerDialog(this, mDateListener, 2017, 8 - 1, 7);
        TimePickerDialog timeDialog = new TimePickerDialog(this, mTimeListener, 15, 24, false);

        switch (v.getId()) {

            case R.id.imageButton_back:

                finish();

                break;

            case R.id.imageButton_save:

                insertSchedule();
                finish();

                break;

            case R.id.textView_date:

                dateDialog.show();

                break;

            case R.id.textView_start:

                mTimeset = SET_START;
                timeDialog.show();

                break;

            case R.id.textView_end:

                mTimeset = SET_END;
                timeDialog.show();

                break;

            case R.id.textView_notification:

                break;
        }
    }

    private DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            monthOfYear = monthOfYear + 1;
            mDateInfo = year + "-" + monthOfYear + "-" + dayOfMonth;
            mDate.setText(mDateInfo);
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            String am_pm;

            if (hourOfDay < 12) {
                am_pm = "AM";
            } else {
                if (hourOfDay != 12) {
                    hourOfDay = hourOfDay - 12;
                }
                am_pm = "PM";
            }

            if (mTimeset == SET_START) {
                mStartInfo = am_pm + " " + hourOfDay + ":" + minute;
                mStart.setText(mStartInfo);
            } else {

                mEndInfo = am_pm + " " + hourOfDay + ":" + minute;
                mEnd.setText(mEndInfo);
            }

            // 설정버튼 눌렀을 때
            Toast.makeText(getApplicationContext(), hourOfDay + "시 " + minute + "분", Toast.LENGTH_SHORT).show();

        }
    };

    private void insertSchedule() {

//        if(isEmptyEditors(mTitle) || isEmptyEditors(mDesc) || isEmptyEditors(mLocation) || isEmptyEditors(mStart) || isEmptyEditors(mEnd)){
//
//            Toast.makeText(this, "모든 항목을 채워 주셔야 합니다", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Async database transaction
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ScheduleData scheduleData;

                if (mPosition == ACTION_CREATE) {
                    scheduleData = mRealm.createObject(ScheduleData.class, getNextKey());
                } else {
                    scheduleData = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();
                }

                scheduleData.setTitle(mTitle.getText().toString());
                scheduleData.setDesc(mDesc.getText().toString());

                scheduleData.setDate(mDate.getText().toString());

                scheduleData.setStartTime(mStart.getText().toString());
                scheduleData.setEndTime(mEnd.getText().toString());

                scheduleData.setAddress(mAddress.getText().toString());

                if (mAllDay.isActivated()) {
                    scheduleData.setAlldaySchedule(true);
                } else {
                    scheduleData.setAlldaySchedule(false);
                }

                if (mFakeCall.isActivated()) {
                    scheduleData.setFakeCall(true);
                } else {
                    scheduleData.setFakeCall(false);
                }
            }
        });
    }

    // check valid about editTexts
    private boolean isEmptyEditors(EditText view) {
        if (TextUtils.isEmpty(view.getText().toString())) {
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
