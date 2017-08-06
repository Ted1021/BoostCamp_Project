package com.tedkim.smartschedule.detail;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ms_square.etsyblur.BlurConfig;
import com.ms_square.etsyblur.BlurDialogFragment;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.ScheduleData;

import io.realm.Realm;

/**
 * @author 김태원
 * @file DetailFragment.java
 * @brief Show detail of selected schedule
 * @date 2017.08.06
 */

public class DetailFragment extends BlurDialogFragment {

    TextView mTitle, mDate, mStart, mEnd, mAddress;

    Realm mRealm;

    static int mPosition = 0;

    public static DetailFragment newInstance(int position) {

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

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        initView(view);

        setData();

        return view;
    }

    private void initView(View view) {

        mTitle = (TextView) view.findViewById(R.id.textView_title);
        mDate = (TextView) view.findViewById(R.id.textView_date);
        mStart = (TextView) view.findViewById(R.id.textView_start);
        mEnd = (TextView) view.findViewById(R.id.textView_end);
        mAddress = (TextView) view.findViewById(R.id.textView_address);

    }

    private void setData() {

        Log.e("CHECK_POSITION", mPosition + "");
        ScheduleData result = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();

        mTitle.setText(result.getTitle());
        mDate.setText(result.getDate());
        mStart.setText(result.getStartTime());
        mEnd.setText(result.getEndTime());
        mAddress.setText(result.getAddress());
    }

    @NonNull
    protected BlurConfig blurConfig() {
        return new BlurConfig.Builder()
                .overlayColor(Color.argb(136, 20, 20, 20))  // semi-transparent white color
                .debug(true)
                .build();
    }
}
