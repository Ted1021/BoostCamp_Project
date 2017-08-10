package com.tedkim.smartschedule.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
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

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.detail.DetailFragment;
import com.tedkim.smartschedule.model.ScheduleData;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * @author 김태원
 * @file ScheduleListRealmAdapter.java
 * @brief RecyclerView Adapter for realm database
 * @date 2017.08.08
 */

public class ScheduleListRealmAdapter extends RealmRecyclerViewAdapter<ScheduleData, ScheduleListRealmAdapter.ViewHolder>
        implements DialogInterface.OnDismissListener{

    Context mContext;
    Activity mActivity;
    LayoutInflater mInflater;

    public ScheduleListRealmAdapter(@Nullable OrderedRealmCollection<ScheduleData> data,
                                    boolean autoUpdate, Context context, Activity activity) {
        super(data, autoUpdate);

        mContext = context;
        mActivity = activity;

        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title, start, end, address;
        CardView scheduleItem;
        LinearLayout itemLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.textView_calendarTitle);
            start = (TextView) itemView.findViewById(R.id.textView_calendarStartTime);
            end = (TextView) itemView.findViewById(R.id.textView_calendarEndTime);
            address = (TextView) itemView.findViewById(R.id.textView_calendarAddress);

            scheduleItem = (CardView) itemView.findViewById(R.id.cardView_scheduleItem);
            itemLayout = (LinearLayout) itemView.findViewById(R.id.layout_scheduleItem);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.layout_calendar_schedule_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ScheduleData data = getItem(position);

        bindData(holder, data);
        setItemAction(holder, data);
    }

    @Override
    public long getItemId(int index) {
        return super.getItemId(index);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    private void bindData(ViewHolder holder, ScheduleData data){

        holder.title.setText(data.getTitle());
        holder.start.setText(data.getStartTime());
        holder.end.setText(data.getEndTime());
        holder.address.setText(data.getAddress());
    }

    private void setItemAction(ViewHolder holder, final ScheduleData data){

        // go to Detail Activity
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFragmentDialog(data.get_id());
            }
        });
    }

    private void setFragmentDialog(long position){

        FragmentManager fragmentManager = ((AppCompatActivity)mContext).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if(prev != null){
            transaction.remove(prev);
        }

        DetailFragment dialog = DetailFragment.newInstance(position);
        dialog.show(fragmentManager, "dialog");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        notifyDataSetChanged();
    }
}
