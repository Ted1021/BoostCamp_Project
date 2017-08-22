package com.tedkim.smartschedule.schedule;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.detail.DetailFragment;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.DateConvertUtil;

import java.util.concurrent.TimeUnit;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * @author 김태원
 * @file ScheduleRouteListAdapter.java
 * @brief RecyclerView Adapter for realm database
 * @date 2017.08.08
 */

public class ScheduleRouteListAdapter extends RealmRecyclerViewAdapter<ScheduleData, ScheduleRouteListAdapter.ViewHolder> implements View.OnClickListener {

    Context mContext;
    LayoutInflater mInflater;
    boolean isExpanded = false;

    public ScheduleRouteListAdapter(@Nullable OrderedRealmCollection<ScheduleData> data, boolean autoUpdate, Context context) {
        super(data, autoUpdate);

        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        // route info
        TextView departInfo, totalTime, transport;
        ImageButton moreInfo;

        // route detail info
        RecyclerView trafficInfoList;

        // schedule info
        TextView title, start, end, address, memo;

        LinearLayout itemLayout, routeInfoLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            departInfo = (TextView) itemView.findViewById(R.id.textView_departInfo);
            totalTime = (TextView) itemView.findViewById(R.id.textView_totalTime);
            transport = (TextView) itemView.findViewById(R.id.textView_transport);
            moreInfo = (ImageButton) itemView.findViewById(R.id.imageButton_moreInfo);

            trafficInfoList = (RecyclerView) itemView.findViewById(R.id.recyclerView_trafficInfoList);

            title = (TextView) itemView.findViewById(R.id.textView_title);
            start = (TextView) itemView.findViewById(R.id.textView_start);
            end = (TextView) itemView.findViewById(R.id.textView_end);
            memo = (TextView) itemView.findViewById(R.id.textView_memo);
            address = (TextView) itemView.findViewById(R.id.textView_address);

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

    private void bindData(ViewHolder holder, ScheduleData data) {

        // 한번이라도 경로를 호출 한 데이터 인 경우,
        // 판단하는 방법) 해당 스케줄의 key 값을 이용해 쿼리를 날리고 결과 값이 0 이 아닌 경우에만 통과하도록 한다
        Realm realm = Realm.getDefaultInstance();

        RealmResults<RouteInfo> routeInfos = realm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll();
        Log.d("CHECK_INIT_SIZE", "Schedule Adapter >>>>>> route info size = " + routeInfos.size());

        // binding traffic info list here
        setRecyclerView(holder, routeInfos);

        if (routeInfos.size() != 0) {

            Log.d("CHECK_INIT_LOCATION", "Schedule Adapter >>>> longitude : " + data.getCurrentLongitude() + " / latitude : " + data.getCurrentLatitude());

            // 최단 시간 기준으로 첫번째 (가장 짦은 시간) 아이템을 보여준다
            holder.routeInfoLayout.setVisibility(View.VISIBLE);
            checkScheduleState(holder, routeInfos.first());
        } else {
            holder.routeInfoLayout.setVisibility(View.GONE);
        }

        holder.title.setText(data.getTitle());
        holder.start.setText(DateConvertUtil.time2string(data.getStartTime()));
        holder.end.setText(DateConvertUtil.time2string(data.getEndTime()));
        holder.address.setText(data.getAddress());
        holder.memo.setText(data.getMemo());

        realm.close();
    }

    private void setRecyclerView(ViewHolder holder, RealmResults<RouteInfo> routeInfos) {

        TrafficInfoListAdapter adapter = new TrafficInfoListAdapter(routeInfos, true, mContext);
        holder.trafficInfoList.setAdapter(adapter);
        Log.d("CHECK_ROUTE_SIZE", "route adapter >>>> " + routeInfos.size());
        adapter.updateData(routeInfos);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.trafficInfoList.setLayoutManager(layoutManager);
    }

    private void setItemAction(final ViewHolder holder, final ScheduleData data) {

        // show more transport information
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isExpanded) {
                    holder.moreInfo.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    holder.moreInfo.setImageResource(R.drawable.ic_action_drop_down);

                    holder.trafficInfoList.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    holder.trafficInfoList.setVisibility(View.GONE);
                } else {
                    holder.moreInfo.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    holder.moreInfo.setImageResource(R.drawable.ic_action_collapse);

                    holder.trafficInfoList.setVisibility(View.VISIBLE);
                    holder.trafficInfoList.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                }
                isExpanded = !isExpanded;
            }
        });

        // go to 'Google Map App'
        holder.address.setOnClickListener(new View.OnClickListener() {
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

    private void setFragmentDialog(String id) {

        FragmentManager fragmentManager = ((AppCompatActivity) mContext).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment prev = fragmentManager.findFragmentByTag("dialog");
        if (prev != null) {
            transaction.remove(prev);
        }

        DetailFragment dialog = DetailFragment.newInstance(id);
        dialog.show(fragmentManager, "dialog");
    }

    private void checkScheduleState(ViewHolder holder, RouteInfo routeInfo) {

        long interval = routeInfo.getDepartTime().getTime() - System.currentTimeMillis();
        long result = TimeUnit.MILLISECONDS.toMinutes(interval);
        Log.d("CHECK_INTERVAL", "schedule adapter ----------- " + result);

        if (result < 0) {
            holder.routeInfoLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorShadow));
            holder.departInfo.setText(DateConvertUtil.time2string(routeInfo.getDepartTime()));
            holder.totalTime.setText(String.format("%d 분", routeInfo.getTotalTime()));
        } else if (result >= 0 && result <= 10) {
            holder.routeInfoLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAlert));
            holder.departInfo.setText(String.format("%d 분 안", result));
            holder.totalTime.setText(String.format("%d 분", routeInfo.getTotalTime()));
        } else {
            holder.routeInfoLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDarkNavy));
            holder.departInfo.setText(DateConvertUtil.time2string(routeInfo.getDepartTime()));
            holder.totalTime.setText(String.format("%d 분", routeInfo.getTotalTime()));
        }
    }

    @Override
    public void onClick(View v) {

    }
}
