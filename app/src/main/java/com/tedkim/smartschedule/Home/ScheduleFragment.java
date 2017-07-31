package com.tedkim.smartschedule.Home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tedkim.smartschedule.R;

/**
 * @file HomeActivity.java
 * @brief Show today & tomorrow schedules with departure time
 *
 * @author 김태원
 * @date 2017.07.31
 */

public class ScheduleFragment extends Fragment {


    public ScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

}
