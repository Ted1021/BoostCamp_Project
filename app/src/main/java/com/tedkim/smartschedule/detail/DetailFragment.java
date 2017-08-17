package com.tedkim.smartschedule.detail;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.regist.RegistActivity;
import com.tedkim.smartschedule.util.AppController;
import com.tedkim.smartschedule.util.DateConvertUtil;

import io.realm.Realm;

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

    Realm mRealm;
    ScheduleData mResult;

    static long mPosition = 0;

    public static final int ACTION_CORRECT = 1;

    public static DetailFragment newInstance(long position) {

        DetailFragment fragment = new DetailFragment();
        mPosition = position;
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
    }

    private void setData() {

        Log.e("CHECK_POSITION", mPosition + "");
        mResult = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();
        mRealm.beginTransaction();

        mTitle.setText(mResult.getTitle());
        mStart.setText(DateConvertUtil.time2string(mResult.getStartTime()));
        mStartDate.setText(mResult.getDate());
        mEnd.setText(DateConvertUtil.time2string(mResult.getEndTime()));
        mEndDate.setText(mResult.getDate());
        mAddress.setText(mResult.getAddress());
        mMemo.setText(mResult.getMemo());

        // 스케줄 데이터가 수정 되면 경로데이터 자체가 모두 초기화
        for(RouteInfo route : mResult.routeInfoList){
            route.routeSequence.deleteAllFromRealm();
        }
        mResult.routeInfoList.deleteAllFromRealm();

        mRealm.commitTransaction();
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

        switch(v.getId()){

            case R.id.button_correct:

                Log.d("CHECK_POSITION", "In Detail Fragment >>>>>>>>>>>> "+mPosition);

                Intent intent = new Intent(getContext(), RegistActivity.class);
                intent.putExtra("POSITION", mPosition);
                intent.putExtra("DATE", mResult.getDate());
                startActivityForResult(intent, AppController.REQ_CORRECT);

                getDialog().hide();

                break;

            case R.id.button_delete:

                mResult = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        // 가장 최 하위에 있는 객체부터 순차적으로 삭제
                        for(RouteInfo route : mResult.routeInfoList){
                            route.routeSequence.deleteAllFromRealm();
                        }
                        mResult.routeInfoList.deleteAllFromRealm();
                        mResult.deleteFromRealm();
                        dismiss();
                    }
                });

                break;
        }
    }

    // TODO - RequestCode Application 객체에 정리해 둘 것
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == AppController.REQ_CORRECT){
            if(resultCode == RESULT_OK){
                setData();
            }
        }
    }
}
