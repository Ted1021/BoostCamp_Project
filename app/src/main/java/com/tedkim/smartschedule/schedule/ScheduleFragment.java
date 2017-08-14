package com.tedkim.smartschedule.schedule;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.Message;
import com.tedkim.smartschedule.model.RouteData;
import com.tedkim.smartschedule.util.AppController;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author 김태원
 * @file ScheduleFragment.java
 * @brief Show today & tomorrow schedules with departure time
 * @date 2017.07.31
 */

public class ScheduleFragment extends Fragment {

    TextView mTime;
    Button mRoute, mCheck;

    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        Log.d("CHECK_ENTER", "Schedule Fragment -------------------");

        initView(view);
        setAction();

        return view;
    }

    private void initView(View view) {

        mTime = (TextView) view.findViewById(R.id.textView_time);
        mRoute = (Button) view.findViewById(R.id.button_route);
        mCheck = (Button) view.findViewById(R.id.button_check);

    }

    private void setRecyclerView() {


    }

    private void setAction() {

        mRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callRouteData();
            }
        });

        mCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selfCheck();
            }
        });
    }

    private void callRouteData() {

        // TODO - 서비스로부터 얻어오는 출발 위치 및 DB 로 부터 얻어오는 도착 위치 필요
        Call<RouteData> routeDataCall = AppController.getRouteInfo()
                .getTransportInfo(127.028584f, 37.263406f, 127.1089531f, 37.4014619f);
        routeDataCall.enqueue(new Callback<RouteData>() {
            @Override
            public void onResponse(Call<RouteData> call, Response<RouteData> response) {

                Log.d("CHECK_CALL", ">>>>>>>>>>>>>> isExecuted? " + call.isExecuted());
                Log.d("CHECK_CALL", ">>>>>>>>>>>>>> isCanceled? " + call.isCanceled());

                if (response.isSuccessful()) {

                    RouteData result = response.body();

                    Log.d("CHECK_RESULT", ">>>>>>>>>>>> 경로 수 : " +result.getResult().getPath().length);
                    Log.d("CHECK_RESULT", ">>>>>>>>>>>> 소요시간 : " +result.getResult().getPath()[0].getInfo().getTotalTime()+"분");

                    mTime.setText(result.getResult().getPath()[0].getInfo().getTotalTime()+"분");

                } else {
                    Log.e("CHECK_FAIL_RETROFIT", "----------- fail to get data");
                }
            }

            @Override
            public void onFailure(Call<RouteData> call, Throwable t) {

                t.printStackTrace();
                Log.e("CHECK_FAIL_SERVER", "----------- server access failure");
            }
        });
    }

    private void selfCheck() {

        Call<Message> messageDataCall = AppController.getRouteInfo().getMessage();
        messageDataCall.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, final Response<Message> response) {

                if (response.isSuccessful()) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("CHECK_FAIL_RETROFIT", "----------- fail to get data");

                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

                t.printStackTrace();
                Log.e("CHECK_FAIL_SERVER", "----------- server access failure");

            }
        });
    }
}
