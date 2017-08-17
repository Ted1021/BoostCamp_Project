package com.tedkim.smartschedule.schedule;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.detail.DetailFragment;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.DateConvertUtil;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * @author 김태원
 * @file ScheduleRouteListAdapter.java
 * @brief RecyclerView Adapter for realm database
 * @date 2017.08.08
 */

public class ScheduleRouteListAdapter extends RealmRecyclerViewAdapter<ScheduleData, ScheduleRouteListAdapter.ViewHolder> {

    Context mContext;
    LayoutInflater mInflater;

    public ScheduleRouteListAdapter(@Nullable OrderedRealmCollection<ScheduleData> data, boolean autoUpdate, Context context) {
        super(data, autoUpdate);

        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        // route info
        TextView departInfo, totalTime, transport;
        ImageButton moreInfo;

        // route detail info

        // schedule info
        TextView title, start, end, address, memo;
        Button search;

        LinearLayout itemLayout, routeInfoLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            departInfo = (TextView) itemView.findViewById(R.id.textView_departInfo);
            totalTime = (TextView) itemView.findViewById(R.id.textView_totalTime);
            transport = (TextView) itemView.findViewById(R.id.textView_transport);
            moreInfo = (ImageButton) itemView.findViewById(R.id.imageButton_moreInfo);

            title = (TextView) itemView.findViewById(R.id.textView_title);
            start = (TextView) itemView.findViewById(R.id.textView_start);
            end = (TextView) itemView.findViewById(R.id.textView_end);
            memo = (TextView) itemView.findViewById(R.id.textView_memo);
            address = (TextView) itemView.findViewById(R.id.textView_address);
            search = (Button) itemView.findViewById(R.id.button_search);

            itemLayout = (LinearLayout) itemView.findViewById(R.id.layout_scheduleItem);
            routeInfoLayout = (LinearLayout) itemView.findViewById(R.id.layout_routeInfo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.layout_schedule_route_item, parent, false);

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

        Log.d("CHECK_INIT_SIZE", ">>>>>> route info size = "+data.routeInfoList.size());
        // 한번이라도 경로를 호출 한 데이터 인 경우,
        if(data.routeInfoList.size() != 0){
            Log.d("CHECK_INIT_LOCATION", ">>>> longitude : "+data.getCurrentLongitude()+" / latitude : "+data.getCurrentLatitude());
            holder.routeInfoLayout.setVisibility(View.VISIBLE);

            holder.departInfo.setText(DateConvertUtil.time2string(data.routeInfoList.get(0).getDepartTime())+"분");
            holder.totalTime.setText(data.routeInfoList.get(0).getTotalTime()+"분");

        }

        holder.title.setText(data.getTitle());
        holder.start.setText(DateConvertUtil.time2string(data.getStartTime()));
        holder.end.setText(DateConvertUtil.time2string(data.getEndTime()));
        holder.address.setText(data.getAddress());
        holder.memo.setText(data.getMemo());
    }

    private void setItemAction(ViewHolder holder, final ScheduleData data){

        // show more transport information
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // go to 'Google Map App'
        holder.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
}
