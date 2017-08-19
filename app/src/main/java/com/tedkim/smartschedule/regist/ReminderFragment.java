package com.tedkim.smartschedule.regist;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.tedkim.smartschedule.R;

import java.util.HashMap;

/**
 * @author 김태원
 * @file ReminderFragment.java
 * @brief Show notification minutes list and add reminder
 * @date 2017.08.19
 */

public class ReminderFragment extends BlurDialogFragment implements View.OnClickListener {

    CardView mLayoutReminder;
    TextView mTimeText;
    CheckedTextView mOnTime, mMin5, mMin15, mMin30, mHour1, mDay1;
    Button mCancel, mSave;

    //    ArrayList<Integer> mTimeList;
    HashMap<Integer, Boolean> mTimeList = new HashMap<>();

    public ReminderFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {

        mLayoutReminder = (CardView)  view.findViewById(R.id.cardView_reminder);
        mLayoutReminder.setOnClickListener(this);

        mTimeText = (TextView) view.findViewById(R.id.textView_timeList);

        mOnTime = (CheckedTextView) view.findViewById(R.id.checkedTextView_onTime);
        mOnTime.setOnClickListener(this);

        mMin5 = (CheckedTextView) view.findViewById(R.id.checkedTextView_5min);
        mMin5.setOnClickListener(this);

        mMin15 = (CheckedTextView) view.findViewById(R.id.checkedTextView_15min);
        mMin15.setOnClickListener(this);

        mMin30 = (CheckedTextView) view.findViewById(R.id.checkedTextView_30min);
        mMin30.setOnClickListener(this);

        mHour1 = (CheckedTextView) view.findViewById(R.id.checkedTextView_1hour);
        mHour1.setOnClickListener(this);

        mDay1 = (CheckedTextView) view.findViewById(R.id.checkedTextView_1day);
        mDay1.setOnClickListener(this);

        mCancel = (Button) view.findViewById(R.id.button_cancel);
        mCancel.setOnClickListener(this);

        mSave = (Button) view.findViewById(R.id.button_save);
        mSave.setOnClickListener(this);
    }

    private void setData() {


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

            case R.id.checkedTextView_onTime:
                setCheckAction(mOnTime, 0);
                break;

            case R.id.checkedTextView_5min:
                setCheckAction(mMin5, 5);
                break;

            case R.id.checkedTextView_15min:
                setCheckAction(mMin15, 15);
                break;

            case R.id.checkedTextView_30min:
                setCheckAction(mMin30, 30);
                break;

            case R.id.checkedTextView_1hour:
                setCheckAction(mHour1, 60);
                break;

            case R.id.checkedTextView_1day:
                setCheckAction(mDay1, 60 * 24);
                break;

            case R.id.button_cancel:
                dismiss();
                break;

            case R.id.button_save:

                break;

            case R.id.cardView_reminder:

                break;
        }
    }

    private void setCheckAction(CheckedTextView view, int min) {

        if (!view.isChecked()) {
            // button image activate
            view.setCheckMarkDrawable(R.drawable.ic_action_checked);
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.colorActivation));
            mTimeList.put(min, true);
            view.setChecked(true);
        } else {
            // button image de-activate
            view.setCheckMarkDrawable(R.drawable.ic_action_unchecked);
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.colorLightGray));
            mTimeList.remove(min);
            view.setChecked(false);
        }
    }
}
