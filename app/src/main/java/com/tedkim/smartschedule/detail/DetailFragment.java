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
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.regist.RegistActivity;

import io.realm.Realm;

import static android.app.Activity.RESULT_OK;

/**
 * @author 김태원
 * @file DetailFragment.java
 * @brief Show detail of selected schedule
 * @date 2017.08.06
 */

public class DetailFragment extends BlurDialogFragment implements View.OnClickListener {

    TextView mTitle, mDate, mStart, mEnd, mAddress;
    Button mCorrect, mDelete;

    Realm mRealm;
    ScheduleData mResult;

    static int mPosition = 0;

    public static final int ACTION_CORRECT = 1;

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

//        R.layout.fragment_detail
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        initView(view);

        setData();

        return view;
    }

    private void initView(View view) {

        mTitle = (TextView) view.findViewById(R.id.textView_title);
//        mDate = (TextView) view.findViewById(R.id.textView_date);
        mStart = (TextView) view.findViewById(R.id.textView_start);
        mEnd = (TextView) view.findViewById(R.id.textView_end);
        mAddress = (TextView) view.findViewById(R.id.textView_address);

        mCorrect = (Button) view.findViewById(R.id.button_correct);
        mCorrect.setOnClickListener(this);

        mDelete = (Button) view.findViewById(R.id.button_delete);
        mDelete.setOnClickListener(this);

    }

    private void setData() {

        Log.e("CHECK_POSITION", mPosition + "");
        mResult = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();

        mTitle.setText(mResult.getTitle());
//        mDate.setText(mResult.getDate());
        mStart.setText(mResult.getStartTime());
        mEnd.setText(mResult.getEndTime());
        mAddress.setText(mResult.getAddress());
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
                startActivityForResult(intent, 100);

                getDialog().hide();

                break;

            case R.id.button_delete:

                mResult = mRealm.where(ScheduleData.class).equalTo("_id", mPosition).findFirst();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mResult.deleteFromRealm();
                        dismiss();
                    }
                });

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100){
            if(resultCode == RESULT_OK){
                setData();
            }
        }
    }
}
