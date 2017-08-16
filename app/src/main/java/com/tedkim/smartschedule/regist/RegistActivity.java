package com.tedkim.smartschedule.regist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ReminderData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.DateConvertUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;

import static com.tedkim.smartschedule.home.HomeActivity.ACTION_CREATE;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // ui components
    ImageButton mBack, mSave;
    EditText mTitle, mMemo, mAddress;
    TextView mDateText, mStartText, mEndText, mSetting, mStartDate, mEndDate;
    CheckBox mAllDay, mFakeCall;
    Button mAddReminder, mSearchLocation;
    LinearLayout mMoreSetting;

    // realm database instance
    Realm mRealm;

    // date info from Home Activity
    long mPosition;

    // dataset from other activity or screen
    String mSelectedAddress;
    RealmList<ReminderData> mReminders;
    ArrayList<String> mStringList = new ArrayList<>();
    double mLatitude, mLongitude;
    Date mStart, mEnd;
    String mDate;

    private static final int SET_START = 0;
    private static final int SET_END = 1;

    int mTimeset = SET_START;

    @Override
    protected void onStop() {
        super.onStop();

        // close Realm database
        mRealm.close();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        // init Realm database
        mRealm = Realm.getDefaultInstance();

        if (getIntent() != null) {
            mPosition = getIntent().getLongExtra("POSITION", ACTION_CREATE);
            mDate = getIntent().getStringExtra("DATE");
        }

        Log.d("CHECK_DATE", "In register >>>>>>>>>>>>>>>>" + mDate);

        initView();

        // 수정 작업일 경우, 이전 내용을 binding 아니면 빈칸으로 Activity 를 시작
        if (mPosition != ACTION_CREATE) {
            setData();
        }
    }

    public void initView() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_save);
        mSave.setOnClickListener(this);

        mTitle = (EditText) findViewById(R.id.editText_title);
        mMemo = (EditText) findViewById(R.id.editText_desc);

        mDateText = (TextView) findViewById(R.id.textView_date);
        mDateText.setText(mDate);
        mDateText.setOnClickListener(this);

        mStart = calendar.getTime();
        mStartText = (TextView) findViewById(R.id.textView_start);
        mStartText.setText(DateConvertUtil.time2string(mStart));
        mStartText.setOnClickListener(this);

        calendar.add(Calendar.HOUR_OF_DAY, 1);
        mEnd = calendar.getTime();
        mEndText = (TextView) findViewById(R.id.textView_end);
        mEndText.setText(DateConvertUtil.time2string(mEnd));
        mEndText.setOnClickListener(this);

        mSearchLocation = (Button) findViewById(R.id.button_searchLocation);
        mSearchLocation.setOnClickListener(this);
        mSearchLocation.setEnabled(false);

        mAddress = (EditText) findViewById(R.id.editText_address);
        mAddress.addTextChangedListener(this);

        mAddReminder = (Button) findViewById(R.id.button_addReminder);
        mAddReminder.setOnClickListener(this);

        mAllDay = (CheckBox) findViewById(R.id.checkBox_allDay);
        mFakeCall = (CheckBox) findViewById(R.id.checkBox_fakeCall);

        mSetting = (TextView) findViewById(R.id.textView_setting);
        mSetting.setOnClickListener(this);

        mMoreSetting = (LinearLayout) findViewById(R.id.layout_setting);

        mStartDate = (TextView) findViewById(R.id.textView_startDate);
        mStartDate.setText(mDate);

        mEndDate = (TextView) findViewById(R.id.textView_endDate);
        mEndDate.setText(mDate);
    }

    private void setData() {

        ScheduleData result = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();
        Log.d("CORRECT", ">>>>>>>>>>>>>>>>> " + mPosition);

        mTitle.setText(result.getTitle());
        mMemo.setText(result.getDesc());
        mDate = result.getDate();
        mDateText.setText(mDate);
        mStart = result.getStartTime();
        mStartText.setText(DateConvertUtil.time2string(result.getStartTime()));
        mEnd = result.getEndTime();
        mEndText.setText(DateConvertUtil.time2string(result.getEndTime()));
        mAddress.setText(result.getAddress());
        mSelectedAddress = result.getAddress();
        mLatitude = result.getLatitude();
        mLongitude = result.getLongitude();
        mAllDay.setChecked(result.isAlldaySchedule());
        mFakeCall.setChecked(result.isFakeCall());

        // TODO - 참여자 표현방법 구상 필요
//        mContacts.setText(result.getContacts());

        for (ReminderData reminder : result.getReminderList()) {
            mStringList.add(reminder.getReminder());
        }
    }

    private DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Date date = calendar.getTime();

            mDate = DateConvertUtil.date2string(date);
            mDateText.setText(mDate);
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            if (mTimeset == SET_START) {
                mStart = calendar.getTime();
                mStartText.setText(DateConvertUtil.time2string(mStart));

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay + 1);
                mEnd = calendar.getTime();
                mEndText.setText(DateConvertUtil.time2string(mEnd));

            } else {
                mEnd = calendar.getTime();
                mEndText.setText(DateConvertUtil.time2string(mEnd));
            }
        }
    };

    private void insertSchedule() {

        // Async database transaction
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ScheduleData scheduleData;

                // 새로운 데이터 생성인 경우
                if (mPosition == ACTION_CREATE) {
                    scheduleData = mRealm.createObject(ScheduleData.class, getNextKey());
                }
                // 기존 데이터의 수정인 경우
                else {
                    scheduleData = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();
                }

                scheduleData.setTitle(mTitle.getText().toString());
                scheduleData.setDesc(mMemo.getText().toString());
                scheduleData.setDate(mDate);
                scheduleData.setStartTime(mStart);
                scheduleData.setEndTime(mEnd);

                scheduleData.setAddress(mSelectedAddress);
                scheduleData.setLatitude(mLatitude);
                scheduleData.setLongitude(mLongitude);
                scheduleData.setReminderList(mReminders);

                if (mAllDay.isChecked()) {
                    scheduleData.setAlldaySchedule(true);
                } else {
                    scheduleData.setAlldaySchedule(false);
                }

                if (mFakeCall.isChecked()) {
                    scheduleData.setFakeCall(true);
                } else {
                    scheduleData.setFakeCall(false);
                }

                Log.d("CHECK_DATE", "Regist Activity >>>>>>>>>>>> "+scheduleData.getDate());
                setResult(RESULT_OK);
            }
        });
    }

    private boolean checkDataValid() {

        // EditText Check
        if (isEmptyEditors(mTitle) || isEmptyEditors(mAddress)) {
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_editText, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // Time Picker Check
        if (mStart.getTime() > mEnd.getTime()) {
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_timePicker, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // address Check
        if (mSelectedAddress == null) {
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_address, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // Reminder Check
        if (mStringList.isEmpty()) {
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_reminder, Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // Check valid about editTexts
    private boolean isEmptyEditors(EditText view) {
        if (TextUtils.isEmpty(view.getText().toString())) {
            view.setError("필수 항목");
            view.requestFocus();
            return true;
        }
        return false;
    }

    // Call Maps Activity
    private void addAddress() {
        Intent intent = new Intent(RegistActivity.this, MapsActivity.class);
        intent.putExtra("ADDRESS", mAddress.getText().toString());
        startActivityForResult(intent, AppController.REQ_GOOGLEMAP);
    }

    // Call Reminder Activity
    private void addReminder() {

        Intent intent = new Intent(RegistActivity.this, ReminderActivity.class);
        startActivityForResult(intent, 102);
    }

    // Realm Object Auto Increment
    // 아직 Auto increment 를 정식지원 하지않음 (latest version : 3.5.0)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // add reminder
        if (requestCode == AppController.REQ_REMINDER) {
            if (resultCode == RESULT_OK) {

                if(data.getStringExtra("REMINDER") != null) {
                    mStringList.add(data.getStringExtra("REMINDER"));
                }
            }
        }

        // search location
        else if (requestCode == AppController.REQ_GOOGLEMAP) {
            if (resultCode == RESULT_OK) {

                mSelectedAddress = data.getStringExtra("ADDRESS");
                mAddress.setText(mSelectedAddress);

                mLatitude = data.getDoubleExtra("LATITUDE", 0);
                mLongitude = data.getDoubleExtra("LONGITUDE", 0);
            }
        }
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
                if (checkDataValid()) {
                    insertSchedule();
                    finish();
                }
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

            case R.id.button_searchLocation:
                addAddress();
                break;

            case R.id.button_addReminder:
                addReminder();
                break;

            case R.id.textView_setting:
                mSetting.setVisibility(View.GONE);
                mMoreSetting.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().length() < 2) {
            mSearchLocation.setEnabled(false);
            mSearchLocation.setTextColor(ContextCompat.getColor(RegistActivity.this, R.color.colorLightGray));
        } else {
            mSearchLocation.setEnabled(true);
            mSearchLocation.setTextColor(ContextCompat.getColor(RegistActivity.this, R.color.colorActivation));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
