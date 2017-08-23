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

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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

import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmObjectChangeListener;
import io.realm.RealmResults;

import static com.tedkim.smartschedule.R.id.imageView_reminder;
import static com.tedkim.smartschedule.util.AppController.REQ_CORRECT;
import static com.tedkim.smartschedule.util.AppController.REQ_GOOGLE_PLACES;

public class RegistActivity extends AppCompatActivity implements View.OnClickListener, OnReminderListSaveListener, TextWatcher {

    // ui components
    ImageButton mBack, mSave;
    EditText mTitle, mMemo;
    TextView mDateText, mStartText, mEndText, mSetting, mStartDate, mEndDate, mReminderText, mMember, mAddress;
    CheckBox mAllDay, mFakeCall;
    Button mAddReminder, mSearchLocation;
    LinearLayout mMoreSetting;
    ImageView mReminderIcon, mAddressIcon, mAllDayIcon, mFakeCallIcon, mMemoIcon, mMemberIcon;

    // realm database instance
    Realm mRealm;
    RealmObjectChangeListener<ScheduleData> mObjectTimeListener;
    RealmObjectChangeListener<ScheduleData> mObjectLocationListener;
    ScheduleData mScheduleData;
    boolean isChanged = false;

    // date info from Home Activity
    String mID;

    // dataset from other activity or screen
    String mSelectedAddress;
    StringBuilder mReminderTextList = new StringBuilder();
    HashMap<Integer, Boolean> mNotificationList;

    double mLatitude, mLongitude;
    Date mStart, mEnd;
    String mDate;

    private int REQ_PLACE_PICKER = 1;
    private static final int SET_START = 0;
    private static final int SET_END = 1;

    int mTimeset = SET_START;
    int mReqCommand = AppController.REQ_CREATE;
    int mSelectedColor, mUnSelectedColor;

    @Override
    protected void onRestart() {
        super.onRestart();
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

        initData();
        Log.d("CHECK_DATE", "In register >>>>>>>>>>>>>>>>" + mDate + " / " + mID);
        initView();

        // 수정 작업일 경우, 이전 내용을 binding 아니면 빈칸으로 Activity 를 시작
        if (!mID.equals("")) {
            mReqCommand = AppController.REQ_CORRECT;
            setData();

            Log.e("CHECK_COMMAND", "regist Activity +++ " + mReqCommand);
        }
    }

    private void initData() {
        // init Realm database
        mRealm = Realm.getDefaultInstance();

        if (getIntent() != null) {
            mID = getIntent().getStringExtra("ID");
            mDate = getIntent().getStringExtra("DATE");
        }

        // init Realm managed object listener
        mObjectTimeListener = new RealmObjectChangeListener<ScheduleData>() {
            @Override
            public void onChange(ScheduleData scheduleData, ObjectChangeSet changeSet) {

                if (changeSet.isFieldChanged("startTime")) {

                    Log.d("CHECK_SCHEDULE_DATA", "Regist Activity >>>>> 시간 변경" + scheduleData.get_id());
//                    mCallback.onObjectFieldChangeListener(scheduleData.get_id());
                }
            }
        };

        mObjectLocationListener = new RealmObjectChangeListener<ScheduleData>() {
            @Override
            public void onChange(ScheduleData scheduleData, ObjectChangeSet changeSet) {
                if (changeSet.isFieldChanged("address")) {
                    Log.d("CHECK_SCHEDULE_DATA", "Regist Activity >>>>> 스케줄 변경" + scheduleData.get_id());
                }
            }
        };

        mNotificationList = new HashMap<Integer, Boolean>() {{

            put(0, false);
            put(5, false);
            put(15, false);
            put(30, false);
            put(60, false);
            put(60 * 24, false);
        }};

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
        mTitle.setSelection(mTitle.getText().length());

        mMemo = (EditText) findViewById(R.id.editText_memo);
        mMemo.addTextChangedListener(this);
        mMemo.setSelection(mMemo.getText().length());
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

        mAddress = (TextView) findViewById(R.id.textView_address);
        mAddress.setOnClickListener(this);
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
        if (result.isAllDaySchedule()) {
            mAllDayIcon.setColorFilter(mSelectedColor);
        }
        mFakeCall.setChecked(result.isFakeCall());
        if (result.isFakeCall()) {
            mFakeCallIcon.setColorFilter(mSelectedColor);
        }

        mReminderIcon.setColorFilter(mSelectedColor);
        mAddressIcon.setColorFilter(mSelectedColor);
        mMemoIcon.setColorFilter(mSelectedColor);

        for (ReminderData reminder : result.reminderList) {

            // reminder fragment 에 전달할 HashMap 데이터셋 초기화
            mNotificationList.put(reminder.getTime(), reminder.isChecked());

            // reminder text 표기
            if (reminder.isChecked()) {
                mReminderTextList.append(DateConvertUtil.minutes2string(RegistActivity.this, reminder.getTime()));
            }
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

                Log.e("CHECK_COMMAND", "Register Activity +++++ " + mReqCommand);

                // 새로운 데이터의 생성인 경우
                if (mReqCommand == AppController.REQ_CREATE) {
                    mScheduleData = mRealm.createObject(ScheduleData.class, UUID.randomUUID().toString());
                    mScheduleData.setBeforeTime(0);
                }

                // 기존 데이터의 수정인 경우
                else {
                    mScheduleData = mRealm.where(ScheduleData.class).equalTo("_id", mID).findFirst();
                    // 경로정보를 받아 온 이력이 있다면,
                    if (mScheduleData.routeInfoList.size() != 0) {

                        // 가장 최하위에 있는 객체부터 순차적으로 삭제
                        RealmResults<RouteSeqData> routeSeqDatas = mRealm.where(RouteSeqData.class).equalTo("_id", mID).findAll();
                        routeSeqDatas.deleteAllFromRealm();

                        RealmResults<RouteInfo> routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", mID).findAll();
                        routeInfos.deleteAllFromRealm();
                    }

                    if (mScheduleData.reminderList.size() != 0) {
                        RealmResults<ReminderData> reminderDatas = mRealm.where(ReminderData.class).equalTo("_id", mID).findAll();
                        reminderDatas.deleteAllFromRealm();
                    }
                }

                mScheduleData.setTitle(mTitle.getText().toString());
                mScheduleData.setMemo(mMemo.getText().toString());
                mScheduleData.setDate(mDate);

                if (mReqCommand == REQ_CORRECT && mScheduleData.getStartTime() != mStart) {
                    isChanged = true;
                }
                mScheduleData.setStartTime(mStart);
                mScheduleData.setEndTime(mEnd);

                if (mReqCommand == REQ_CORRECT && mScheduleData.getAddress() != mSelectedAddress) {
                    isChanged = true;
                }
                mScheduleData.setAddress(mSelectedAddress);
                mScheduleData.setLatitude(mLatitude);
                mScheduleData.setLongitude(mLongitude);

                Map<Integer, Boolean> keySort = new TreeMap<>(mNotificationList);
                for (Map.Entry<Integer, Boolean> data : keySort.entrySet()) {

                    ReminderData reminderData = mRealm.createObject(ReminderData.class);

                    reminderData.set_id(mScheduleData.get_id());
                    reminderData.setTime(data.getKey());
                    reminderData.setChecked(data.getValue());

                    mScheduleData.reminderList.add(reminderData);
                }

                if (mAllDay.isChecked()) {
                    mScheduleData.setAllDaySchedule(true);
                    mAllDayIcon.setColorFilter(mSelectedColor);

                } else {
                    mScheduleData.setAllDaySchedule(false);
                }

                if (mFakeCall.isChecked()) {
                    mScheduleData.setFakeCall(true);
                    mFakeCallIcon.setColorFilter(mSelectedColor);

                } else {
                    mScheduleData.setFakeCall(false);
                }
            }
        });
    }

    private boolean checkDataValid() {

        boolean checkNotiList = false;

        // EditText Check
        if (isEmptyEditors(mTitle)) {
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
        // 리마인더 hashMap 이 모두 false 이면 ....
        TreeMap<Integer, Boolean> valueSet = new TreeMap<>(mNotificationList);
        for (Boolean check : valueSet.values()) {

            if (check) {
                checkNotiList = true;
            }
        }
        if (!checkNotiList) {
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

        String address = mAddress.getText().toString();

        if (address.equals("") || address.isEmpty() || address.equals("주소 없음")) {
            Snackbar.make(getWindow().getDecorView().getRootView(), R.string.error_message_no_address, Snackbar.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(RegistActivity.this, MapsActivity.class);
            intent.putExtra("ADDRESS", mAddress.getText().toString());
            startActivityForResult(intent, AppController.REQ_GOOGLE_MAP);
        }
    }

    private void addReminder() {

        FragmentManager fragmentManager = RegistActivity.this.getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag("reminder");
        if (prev != null) {
            transaction.remove(prev);
        }
        transaction.commit();

        ReminderFragment dialog = ReminderFragment.newInstance(mNotificationList);
        dialog.show(fragmentManager, "reminder");


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
                addReminder();
                break;

            case R.id.textView_setting:
                mSetting.setVisibility(View.GONE);
                mMoreSetting.setVisibility(View.VISIBLE);
                break;

            case R.id.textView_address:

                Intent intent = null;
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(RegistActivity.this);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                startActivityForResult(intent, REQ_GOOGLE_PLACES);

                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // search location
        if (requestCode == AppController.REQ_GOOGLE_MAP) {
            if (resultCode == RESULT_OK) {

                mSelectedAddress = data.getStringExtra("ADDRESS");
                mAddress.setText(mSelectedAddress);
                mAddressIcon.setColorFilter(mSelectedColor);

                mLatitude = data.getDoubleExtra("LATITUDE", 0);
                mLongitude = data.getDoubleExtra("LONGITUDE", 0);
            }
        }

        // places api
        if (requestCode == REQ_GOOGLE_PLACES) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(RegistActivity.this, data);
                mAddress.setText(place.getAddress());
            }
        }
    }

    @Override
    public void onReminderListChangedListener(HashMap<Integer, Boolean> notificationList) {

        // 기존 데이터 초기화
        mReminderTextList.setLength(0);

        // 변경 된 데이터 삽입
        mNotificationList.putAll(notificationList);
        Map<Integer, Boolean> keySort = new TreeMap<>(mNotificationList);

        for (Map.Entry<Integer, Boolean> map : keySort.entrySet()) {

            if (map.getValue()) {
                mReminderTextList.append(DateConvertUtil.minutes2string(RegistActivity.this, map.getKey()));
            }
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

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().length() < 2) {
            mMemoIcon.setColorFilter(ContextCompat.getColor(RegistActivity.this, R.color.colorLightGray));
        } else {
            mMemoIcon.setColorFilter(ContextCompat.getColor(RegistActivity.this, R.color.colorActivation));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
