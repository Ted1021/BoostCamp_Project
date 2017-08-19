package com.tedkim.smartschedule.regist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ReminderData;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.RouteSeqData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.DateConvertUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.tedkim.smartschedule.R.id.imageView_reminder;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, OnReminderListSaveListener {

    // ui components
    ImageButton mBack, mSave;
    EditText mTitle, mMemo, mAddress;
    TextView mDateText, mStartText, mEndText, mSetting, mStartDate, mEndDate, mReminderText, mMember;
    CheckBox mAllDay, mFakeCall;
    Button mAddReminder, mSearchLocation;
    LinearLayout mMoreSetting;
    ImageView mReminderIcon, mAddressIcon, mAllDayIcon, mFakeCallIcon, mMemoIcon, mMemberIcon;

    // realm database instance
    Realm mRealm;

    // date info from Home Activity
    String mID;

    // dataset from other activity or screen
    String mSelectedAddress;
    StringBuilder mReminderTextList = new StringBuilder();
    HashMap<Integer, Boolean> mNotificationList = new HashMap<>();

    double mLatitude, mLongitude;
    Date mStart, mEnd;
    String mDate;

    private static final int SET_START = 0;
    private static final int SET_END = 1;

    int mTimeset = SET_START;
    int mReqCommand = AppController.REQ_CREATE;

    int mSelectedColor, mUnSelectedColor;

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
            mID = getIntent().getStringExtra("ID");
            mDate = getIntent().getStringExtra("DATE");
        }

        Log.d("CHECK_DATE", "In register >>>>>>>>>>>>>>>>" + mDate + " / " + mID);

        initView();

        // 수정 작업일 경우, 이전 내용을 binding 아니면 빈칸으로 Activity 를 시작
        if (!mID.equals("")) {
            mReqCommand = AppController.REQ_CORRECT;
            setData();

            Log.e("CHECK_COMMAND", "regist Activity +++ " + mReqCommand);
        }
    }

    public void initView() {

        mSelectedColor = ContextCompat.getColor(RegistActivity.this, R.color.colorActivation);
        mUnSelectedColor = ContextCompat.getColor(RegistActivity.this, R.color.colorLightGray);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        mBack = (ImageButton) findViewById(R.id.imageButton_back);
        mBack.setOnClickListener(this);

        mSave = (ImageButton) findViewById(R.id.imageButton_save);
        mSave.setOnClickListener(this);

        mTitle = (EditText) findViewById(R.id.editText_title);

        mMemo = (EditText) findViewById(R.id.editText_memo);
        mMemoIcon = (ImageView) findViewById(R.id.imageView_memo);

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
        mAddressIcon = (ImageView) findViewById(R.id.imageView_location);

        mAddReminder = (Button) findViewById(R.id.button_addReminder);
        mAddReminder.setOnClickListener(this);

        mAllDay = (CheckBox) findViewById(R.id.checkBox_allDay);
        mAllDayIcon = (ImageView) findViewById(R.id.imageView_allDay);

        mFakeCall = (CheckBox) findViewById(R.id.checkBox_fakeCall);
        mFakeCallIcon = (ImageView) findViewById(R.id.imageView_fakeCall);

        mSetting = (TextView) findViewById(R.id.textView_setting);
        mSetting.setOnClickListener(this);

        mMoreSetting = (LinearLayout) findViewById(R.id.layout_setting);

        mStartDate = (TextView) findViewById(R.id.textView_startDate);
        mStartDate.setText(mDate);

        mEndDate = (TextView) findViewById(R.id.textView_endDate);
        mEndDate.setText(mDate);

        mReminderText = (TextView) findViewById(R.id.textView_reminder);
        mReminderIcon = (ImageView) findViewById(imageView_reminder);

        mMember = (TextView) findViewById(R.id.textView_member);
        mMemberIcon = (ImageView) findViewById(R.id.imageView_member);
    }

    private void setData() {

        ScheduleData result = mRealm.where(ScheduleData.class).equalTo("_id", mID).findFirst();
        Log.d("CORRECT", "registActivity >>>>>>> " + mID);

        mTitle.setText(result.getTitle());
        mMemo.setText(result.getMemo());
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
        mAllDay.setChecked(result.isAllDaySchedule());
        if(result.isAllDaySchedule()){
            mAllDayIcon.setColorFilter(mSelectedColor);
        }
        mFakeCall.setChecked(result.isFakeCall());
        if(result.isFakeCall()){
            mFakeCallIcon.setColorFilter(mSelectedColor);
        }

        mReminderIcon.setColorFilter(mSelectedColor);
        mAddressIcon.setColorFilter(mSelectedColor);
        mMemoIcon.setColorFilter(mSelectedColor);

        for (ReminderData reminder : result.reminderList) {

            // reminder fragment 에 전달할 HashMap 데이터셋 초기화
            mNotificationList.put(reminder.getTime(), reminder.isChecked());

            // reminder text 표기
            mReminderTextList.append(DateConvertUtil.minutes2string(RegistActivity.this, reminder.getTime()));
        }
        mReminderText.setText(mReminderTextList);
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

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                ScheduleData scheduleData;

                Log.e("CHECK_COMMAND", "Register Activity +++++ " + mReqCommand);

                // 새로운 데이터의 생성인 경우
                if (mReqCommand == AppController.REQ_CREATE) {
                    scheduleData = mRealm.createObject(ScheduleData.class, UUID.randomUUID().toString());
                }

                // 기존 데이터의 수정인 경우
                else {
                    scheduleData = mRealm.where(ScheduleData.class).equalTo("_id", mID).findFirst();
                    // 경로정보를 받아 온 이력이 있다면,
                    if (scheduleData.routeInfoList.size() != 0) {

                        // 가장 최하위에 있는 객체부터 순차적으로 삭제
                        RealmResults<RouteSeqData> routeSeqDatas = mRealm.where(RouteSeqData.class).equalTo("_id", mID).findAll();
                        routeSeqDatas.deleteAllFromRealm();

                        RealmResults<RouteInfo> routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", mID).findAll();
                        routeInfos.deleteAllFromRealm();
                    }

                    if (scheduleData.reminderList.size() != 0) {
                        RealmResults<ReminderData> reminderDatas = mRealm.where(ReminderData.class).equalTo("_id", mID).findAll();
                        reminderDatas.deleteAllFromRealm();
                    }
                }

                scheduleData.setTitle(mTitle.getText().toString());
                scheduleData.setMemo(mMemo.getText().toString());
                scheduleData.setDate(mDate);
                scheduleData.setStartTime(mStart);
                scheduleData.setEndTime(mEnd);
                scheduleData.setAddress(mSelectedAddress);
                scheduleData.setLatitude(mLatitude);
                scheduleData.setLongitude(mLongitude);

                Map<Integer, Boolean> keySort = new TreeMap<>(mNotificationList);
                for (Map.Entry<Integer, Boolean> data : keySort.entrySet()) {

                    ReminderData reminderData = mRealm.createObject(ReminderData.class);

                    reminderData.set_id(scheduleData.get_id());
                    reminderData.setTime(data.getKey());
                    reminderData.setChecked(data.getValue());

                    scheduleData.reminderList.add(reminderData);
                }

                if (mAllDay.isChecked()) {
                    scheduleData.setAllDaySchedule(true);
                    mAllDayIcon.setColorFilter(mSelectedColor);

                } else {
                    scheduleData.setAllDaySchedule(false);
                }

                if (mFakeCall.isChecked()) {
                    scheduleData.setFakeCall(true);
                    mFakeCallIcon.setColorFilter(mSelectedColor);

                } else {
                    scheduleData.setFakeCall(false);
                }

                Log.d("CHECK_DATE", "Regist Activity >>> " + scheduleData.getDate());
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
        if (mNotificationList.size() == 0) {
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

    private void addReminder() {

        FragmentManager fragmentManager = RegistActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag("reminder");
        if (prev != null) {
            transaction.remove(prev);
        }

        ReminderFragment dialog = ReminderFragment.newInstance(mNotificationList);
        dialog.show(fragmentManager, "reminder");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // search location
        if (requestCode == AppController.REQ_GOOGLEMAP) {
            if (resultCode == RESULT_OK) {

                mSelectedAddress = data.getStringExtra("ADDRESS");
                mAddress.setText(mSelectedAddress);
                mAddressIcon.setColorFilter(mSelectedColor);

                mLatitude = data.getDoubleExtra("LATITUDE", 0);
                mLongitude = data.getDoubleExtra("LONGITUDE", 0);
            }
        }
    }

    @Override
    public void onClick(View v) {

        Date currentDate = DateConvertUtil.string2date(mDate);

        DatePickerDialog dateDialog = new DatePickerDialog(this, mDateListener, DateConvertUtil.yearFromDate(currentDate), DateConvertUtil.monthFromDate(currentDate), DateConvertUtil.dayFromDate(currentDate));
        TimePickerDialog timeDialog = new TimePickerDialog(this, mTimeListener, DateConvertUtil.hourOfDayFromDate(mStart), DateConvertUtil.minutesFromDate(mStart), false);

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
                mNotificationList.clear();
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

    @Override
    public void onReminderListChangedListener(HashMap<Integer, Boolean> notificationList) {

        // 기존 데이터 초기화
        mReminderTextList.setLength(0);

        // 변경 된 데이터 삽입
        mNotificationList.putAll(notificationList);
        Map<Integer, Boolean> keySort = new TreeMap<>(mNotificationList);
        for (int key : keySort.keySet()) {
            mReminderTextList.append(DateConvertUtil.minutes2string(RegistActivity.this, key));
        }
        // 데이터 최종 출력
        if (mNotificationList.size() != 0) {
            mReminderText.setText(mReminderTextList);
            mReminderIcon.setColorFilter(mSelectedColor);
        } else {
            mReminderText.setText(getString(R.string.reminder_type_noItem));
            mReminderIcon.setColorFilter(mUnSelectedColor);
        }
    }
}
