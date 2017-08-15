package com.tedkim.smartschedule.regist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.github.sundeepk.compactcalendarview.domain.Event;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ReminderData;
import com.tedkim.smartschedule.model.ScheduleData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

import static com.tedkim.smartschedule.home.HomeActivity.ACTION_CREATE;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    // ui components
    Toolbar mToolbar;
    ImageButton mBack, mSave;
    EditText mTitle, mMemo, mAddress, mContacts;
    TextView mDate, mStart, mEnd;
    CheckBox mAllDay, mFakeCall;
    Button mAddReminder, mSearchLocation;
    ListView mReminderList;

    List<Event> mEvents;

    // realm database instance
    Realm mRealm;

    // date info from Home Activity
    long mPosition;
    String mDateInfo, mStartInfo, mEndInfo;

    // dataset from other activity or screen
    String mSelectedAddress;
    RealmList<ReminderData> mReminders;
    ArrayList<String> mStringList = new ArrayList<>();
    ArrayAdapter<String> mAdapter;
    double mLatitude, mLongitude;
    int mStartHour, mEndHour, mStartMin, mEndMin;

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

        if(getIntent() != null){
            mPosition = getIntent().getLongExtra("POSITION", ACTION_CREATE);
            mDateInfo = getIntent().getStringExtra("DATE");
        }

        Log.d("CHECK_DATE", "In register >>>>>>>>>>>>>>>>" + mDateInfo);

        initView();

        // 수정 작업일 경우, 이전 내용을 binding 아니면 빈칸으로 Activity 를 시작
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
        mMemo = (EditText) findViewById(R.id.editText_desc);

        mDate = (TextView) findViewById(R.id.textView_date);
        mDate.setText(mDateInfo);
        mDate.setOnClickListener(this);

        mStart = (TextView) findViewById(R.id.textView_start);
        mStart.setOnClickListener(this);

        mEnd = (TextView) findViewById(R.id.textView_end);
        mEnd.setOnClickListener(this);

        mSearchLocation = (Button) findViewById(R.id.button_searchLocation);
        mSearchLocation.setOnClickListener(this);
        mSearchLocation.setEnabled(false);

        mAddress = (EditText) findViewById(R.id.editText_address);
        mAddress.addTextChangedListener(this);

        mContacts = (EditText) findViewById(R.id.editText_contacts);

        mReminderList = (ListView) findViewById(R.id.listView_reminderList);

        mAddReminder = (Button) findViewById(R.id.button_addReminder);
        mAddReminder.setOnClickListener(this);

        mAdapter = new ArrayAdapter<>(RegistActivity.this, android.R.layout.simple_list_item_1, mStringList);
        mReminderList.setAdapter(mAdapter);

        mAllDay = (CheckBox) findViewById(R.id.checkBox_allDay);
        mFakeCall = (CheckBox) findViewById(R.id.checkBox_fakeCall);
    }

    private void setData() {

        ScheduleData result = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();

        Log.d("CORRECT",">>>>>>>>>>>>>>>>> "+mPosition);

        mTitle.setText(result.getTitle());
        mMemo.setText(result.getDesc());
        mStart.setText(result.getStartTime());
        mEnd.setText(result.getEndTime());
        mAddress.setText(result.getAddress());
        mSelectedAddress = result.getAddress();
        mLatitude = result.getLatitude();
        mLongitude = result.getLongitude();
        mAllDay.setChecked(result.isAlldaySchedule());
        mFakeCall.setChecked(result.isFakeCall());
        // TODO - 참여자 표현방법 구상 필요
//        mContacts.setText(result.getContacts());

        for(ReminderData reminder : result.getReminderList()){
            mStringList.add(reminder.getReminder());
        }
        mAdapter.notifyDataSetChanged();
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

            SimpleDateFormat form = new SimpleDateFormat("hh:mm");
            Calendar calendar = Calendar.getInstance();

            // TODO - Hour, HourOfDay 의 차이점은? & DateUtils 의 정확한 사용법 알아볼것
            // 얘가 12hours / 24hours 포맷 전부 지원하고 AM/PM 여부 또한 계산해줌.
            // 하지만, 파라미터가 milliseconds 임
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            if (hourOfDay < 12) {
                am_pm = "AM";
            } else {
                if (hourOfDay != 12) {
                    hourOfDay = hourOfDay - 12;
                }
                am_pm = "PM";
            }

            if (mTimeset == SET_START) {
                mStartHour = hourOfDay;
                mStartMin = minute;
                mStartInfo = am_pm + " " + hourOfDay + ":" + minute;
                mStart.setText(am_pm+" "+form.format(calendar.getTime()));
            } else {
                mEndHour = hourOfDay;
                mEndMin = minute;
                mEndInfo = am_pm + " " + hourOfDay + ":" + minute;
                mEnd.setText(am_pm+" "+form.format(calendar.getTime()));
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
                scheduleData.setDate(mDate.getText().toString());
                scheduleData.setStartTime(mStart.getText().toString());
                scheduleData.setEndTime(mEnd.getText().toString());

                // TODO - '입력' 이나 '수정' 과정에서 어떻게 대중교통데이터를 받아 올 지 고민해 볼 것
                scheduleData.setDepartTime(mStart.getText().toString());
                Log.i("CHECK_REGIST", "-------- "+scheduleData.getDepartTime());

                scheduleData.setTotalTime(0);
                Log.i("CHECK_REGIST", "-------- "+scheduleData.getTotalTime());

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

                setResult(RESULT_OK);
            }
        });
    }

    private boolean checkDataValid(){

        // EditText Check
        if(isEmptyEditors(mTitle) || isEmptyEditors(mAddress)){
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_editText, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // Time Picker Check (Only Start Time)
        if((mStartHour > mEndHour) || (mStartMin > mEndMin)){
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_timePicker, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // address Check
        if(mSelectedAddress == null){
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_address, Snackbar.LENGTH_LONG).show();
            return false;
        }

        // Reminder Check
        if(mStringList.isEmpty()){
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
    private void addAddress(){
        Intent intent = new Intent(RegistActivity.this, MapsActivity.class);
        intent.putExtra("ADDRESS", mAddress.getText().toString());
        startActivityForResult(intent, 103);
    }

    // Call Reminder Activity
    private void addReminder(){

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

    // TODO - RequestCode 모두 Application 객체에 모아 둘 것
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // add reminder
        if(requestCode == 102){
            if(resultCode == RESULT_OK){

                mStringList.add(data.getStringExtra("REMINDER"));
                mAdapter.notifyDataSetChanged();
            }
        }

        // search location
        else if(requestCode == 103){
            if(resultCode == RESULT_OK){

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
                if(checkDataValid()){
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
