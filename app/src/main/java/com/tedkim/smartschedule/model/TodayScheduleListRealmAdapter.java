package com.tedkim.smartschedule.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * @author 김태원
 * @file TodayScheduleListRealmAdapter.java
 * @brief RecyclerView Adapter for realm database
 * @date 2017.08.08
 */

public class TodayScheduleListRealmAdapter extends RealmRecyclerViewAdapter<ScheduleData, TodayScheduleListRealmAdapter.ViewHolder> {

    Context mContext;

    public TodayScheduleListRealmAdapter(@Nullable OrderedRealmCollection<ScheduleData> data, boolean autoUpdate, Context context) {
        super(data, autoUpdate);

        mContext = context;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }
}
