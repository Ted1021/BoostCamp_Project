package com.tedkim.smartschedule.detail;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ReminderData;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.RouteSeqData;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.regist.RegistActivity;
import com.tedkim.smartschedule.service.alarm.AlarmService;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.DateConvertUtil;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;

/**
 * @author 김태원
 * @file DetailFragment.java
 * @brief Show detail of selected schedule
 * @date 2017.08.06
 */

public class DetailFragment extends BlurDialogFragment implements View.OnClickListener {

    TextView mTitle, mStart, mStartDate, mEnd, mEndDate, mAddress, mMemo, mReminder;
    Button mCorrect, mDelete;
    CardView mScheduleDetail;

    Realm mRealm;
    ScheduleData mResult;

    static String mID;
    StringBuilder mReminderTextList = new StringBuilder();

    public static final int ACTION_CORRECT = 1;

    public static DetailFragment newInstance(String id) {

        DetailFragment fragment = new DetailFragment();
        mID = id;
        fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.EtsyBlurDialogTheme);

        return fragment;
    }

    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mRealm = Realm.getDefaultInstance();

//        R.layout.fragment_detail
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        initView(view);

        setData();

        return view;
    }

    private void initView(View view) {

        mTitle = (TextView) view.findViewById(R.id.textView_title);

        mStart = (TextView) view.findViewById(R.id.textView_start);
        mStartDate = (TextView) view.findViewById(R.id.textView_startDate);

        mEnd = (TextView) view.findViewById(R.id.textView_end);
        mEndDate = (TextView) view.findViewById(R.id.textView_endDate);

        mAddress = (TextView) view.findViewById(R.id.textView_address);
        mMemo = (TextView) view.findViewById(R.id.textView_memo);
        mReminder = (TextView) view.findViewById(R.id.textView_reminder);

        mCorrect = (Button) view.findViewById(R.id.button_correct);
        mCorrect.setOnClickListener(this);
        mDelete = (Button) view.findViewById(R.id.button_delete);
        mDelete.setOnClickListener(this);

        mScheduleDetail = (CardView) view.findViewById(R.id.cardView_scheduleDetail);
        mScheduleDetail.setOnClickListener(this);
    }

    private void setData() {

        Log.e("CHECK_ID", "+++++++++++ in Detail Fragment " + mID);
        mResult = mRealm.where(ScheduleData.class).equalTo("_id", mID).findFirst();

        mTitle.setText(mResult.getTitle());
        mStart.setText(DateConvertUtil.time2string(mResult.getStartTime()));
        mStartDate.setText(mResult.getDate());
        mEnd.setText(DateConvertUtil.time2string(mResult.getEndTime()));
        mEndDate.setText(mResult.getDate());
        mAddress.setText(mResult.getAddress());
        mMemo.setText(mResult.getMemo());

        for (ReminderData reminder : mResult.reminderList) {

            // reminder text 표기
            if(reminder.isChecked()) {
                mReminderTextList.append(DateConvertUtil.minutes2string(getContext(), reminder.getTime()));
            }
        }
        mReminder.setText("");
        mReminder.setText(mReminderTextList);
    }

    @NonNull
    protected BlurConfig blurConfig() {
        return new BlurConfig.Builder()
                .overlayColor(Color.argb(136, 20, 20, 20))
                .debug(true)
                .build();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_correct:

                Log.d("CHECK_ID", "In Detail Fragment >>>>>>>>>>>> " + mID);

                Intent intent = new Intent(getContext(), RegistActivity.class);
                intent.putExtra("ID", mID);
                intent.putExtra("DATE", mResult.getDate());
                startActivityForResult(intent, AppController.REQ_CORRECT);

                getDialog().hide();

                break;

            case R.id.button_delete:

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        Log.e("CHECK_DELETE", "<<<<<<<<<< start deleting object " + mID);

                        // 설정 된 알람 삭제
                        AlarmService.removeAlarm(getContext(), mID);

                        // 가장 최하위에 있는 객체부터 순차적으로 삭제
                        RealmResults<RouteSeqData> routeSeqDatas = mRealm.where(RouteSeqData.class).equalTo("_id", mID).findAll();
                        routeSeqDatas.deleteAllFromRealm();
                        Log.e("CHECK_DELETE_SEQ", "<<<<<<<<<<<<<< size " + routeSeqDatas.size());

                        RealmResults<RouteInfo> routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", mID).findAll();
                        routeInfos.deleteAllFromRealm();
                        Log.e("CHECK_DELETE_INFO", "<<<<<<<<<<<<<<size " + routeInfos.size());


                        mResult = mRealm.where(ScheduleData.class).equalTo("_id", mID).findFirst();
                        mResult.deleteFromRealm();
                        Log.e("CHECK_DELETE_OBJECT", "<<<<<<<<<<<<<< size ");

                        dismiss();
                    }
                });
                break;

            case R.id.cardView_scheduleDetail:

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppController.REQ_CORRECT) {
            if (resultCode == RESULT_OK) {
                setData();
            }
        }
    }
}
