package com.tedkim.smartschedule.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.detail.DetailActivity;
import com.tedkim.smartschedule.model.ScheduleData;

import java.util.ArrayList;

import top.wefor.circularanim.CircularAnim;

/**
 * @author 김태원
 * @file CalendarScheduleListAdapter.java
 * @brief Calendar RecyclerView Adapter
 * @date 2017.08.03
 */

public class CalendarScheduleListAdapter extends RecyclerView.Adapter<CalendarScheduleListAdapter.ScheduleViewHolder> {

    Context mContext;
    Activity mActivity;

    ArrayList<ScheduleData> mDataset = new ArrayList<>();
    LayoutInflater mLayoutInflater;

    public CalendarScheduleListAdapter(Context context, Activity activity, ArrayList<ScheduleData> dataset) {

        mContext = context;
        mActivity = activity;
        mDataset = dataset;

        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {

        TextView title, start, end, address;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            setView(itemView);

            setAction(itemView);
        }

        private void setView(View itemView) {

            title = (TextView) itemView.findViewById(R.id.textView_calendarTitle);
            start = (TextView) itemView.findViewById(R.id.textView_calendarStartTime);
            end = (TextView) itemView.findViewById(R.id.textView_calendarEndTime);
            address = (TextView) itemView.findViewById(R.id.textView_calendarAddress);
        }

        private void setAction(View itemView){

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CircularAnim.fullActivity(mActivity, v)
                            .colorOrImageRes(R.color.colorAppTheme)
                            .go(new CircularAnim.OnAnimationEndListener() {
                                @Override
                                public void onAnimationEnd() {

                                    Intent intent = new Intent(mActivity, DetailActivity.class);
                                    mContext.startActivity(intent);
                                }
                            });
                }
            });
        }
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.layout_calendar_schedule_item, parent, false);

        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {

        ScheduleData data = mDataset.get(position);

        holder.title.setText(data.getTitle());
        holder.start.setText(data.getStartTime());
        holder.end.setText(data.getEndTime());
        holder.address.setText(data.getAddress());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
