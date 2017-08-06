package com.tedkim.smartschedule.calendar;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.detail.DetailFragment;
import com.tedkim.smartschedule.model.ScheduleData;

import java.util.ArrayList;

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
        CardView scheduleItem;

        SwipeLayout item;
        LinearLayout itemLayout;

        public ScheduleViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textView_calendarTitle);
            start = (TextView) itemView.findViewById(R.id.textView_calendarStartTime);
            end = (TextView) itemView.findViewById(R.id.textView_calendarEndTime);
            address = (TextView) itemView.findViewById(R.id.textView_calendarAddress);

            scheduleItem = (CardView) itemView.findViewById(R.id.cardView_scheduleItem);

            // TODO - swipe layout test
            item = (SwipeLayout) itemView.findViewById(R.id.swipeLayout_item);
            item.setShowMode(SwipeLayout.ShowMode.PullOut);

            itemLayout = (LinearLayout) itemView.findViewById(R.id.layout_scheduleItem);
        }
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mLayoutInflater.inflate(R.layout.layout_calendar_schedule_item, parent, false);

        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, final int position) {

        final ScheduleData data = mDataset.get(position);

        bindData(holder, data);

        setItemAction(holder, data);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void bindData(ScheduleViewHolder holder, ScheduleData data){

        holder.title.setText(data.getTitle());
        holder.start.setText(data.getStartTime());
        holder.end.setText(data.getEndTime());
        holder.address.setText(data.getAddress());
    }

    private void setItemAction(ScheduleViewHolder holder, final ScheduleData data){

        // go to Detail Activity
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFragmentDialog(data.get_id());
            }
        });

        // TODO - Swipe layout action here
        // swipe layout for item
        holder.item.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(SwipeLayout layout) {

            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
    }

    private void setFragmentDialog(int position){

        FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if(prev != null){
            transaction.remove(prev);
        }

        DetailFragment dialog = DetailFragment.newInstance(position);
        dialog.show(fragmentManager, "dialog");
    }
}
