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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.detail.DetailFragment;
import com.tedkim.smartschedule.model.BeforeTimeMessage;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.ScheduleData;
import com.tedkim.smartschedule.util.DateConvertUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
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
    Realm mRealm;
    boolean isExpanded = false;

    private static final int SORT_DIST = 0;
    private static final int SORT_TIME = 1;
    private static final int SORT_TRANSIT = 2;

    private static final int SET_ON_TIME = 0;
    private static final int SET_15_MIN = 1;
    private static final int SET_30_MIN = 2;
    private static final int SET_60_MIN = 3;

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
        Spinner sortType, setDepartTime;
        RecyclerView trafficInfoList;

        // schedule info
        TextView title, start, end, address, memo;

        LinearLayout itemLayout, routeInfoLayout, trafficInfoLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            departInfo = (TextView) itemView.findViewById(R.id.textView_departInfo);
            totalTime = (TextView) itemView.findViewById(R.id.textView_totalTime);
            transport = (TextView) itemView.findViewById(R.id.textView_transport);
            moreInfo = (ImageButton) itemView.findViewById(R.id.imageButton_moreInfo);
            sortType = (Spinner) itemView.findViewById(R.id.spinner_trafficType);
            setDepartTime = (Spinner) itemView.findViewById(R.id.spinner_setDepartTime);

            trafficInfoList = (RecyclerView) itemView.findViewById(R.id.recyclerView_trafficInfoList);

            title = (TextView) itemView.findViewById(R.id.textView_title);
            start = (TextView) itemView.findViewById(R.id.textView_start);
            end = (TextView) itemView.findViewById(R.id.textView_end);
            memo = (TextView) itemView.findViewById(R.id.textView_memo);
            address = (TextView) itemView.findViewById(R.id.textView_address);

            itemLayout = (LinearLayout) itemView.findViewById(R.id.layout_scheduleItem);
            routeInfoLayout = (LinearLayout) itemView.findViewById(R.id.layout_routeInfo);
            trafficInfoLayout = (LinearLayout) itemView.findViewById(R.id.layout_trafficInfo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.layout_schedule_route_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        mRealm = Realm.getDefaultInstance();
        ScheduleData data = getItem(position);
        String sortType = "totalDistance";

        // set SubPath Info RecyclerView
        RealmResults<RouteInfo> routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll();
        TrafficInfoListAdapter mAdapter = new TrafficInfoListAdapter(routeInfos, true, mContext, holder);
        holder.trafficInfoList.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.trafficInfoList.setLayoutManager(layoutManager);

        bindData(holder, data);
        setItemAction(holder, data, mAdapter);

        mRealm.close();
    }


    private void bindData(ViewHolder holder, ScheduleData data) {

        // 한번이라도 경로를 호출 한 데이터 인 경우,
        if (data.routeInfoList.size() != 0) {
            holder.routeInfoLayout.setVisibility(View.VISIBLE);
            checkScheduleState(holder, data);
        } else {
            holder.routeInfoLayout.setVisibility(View.GONE);
        }

        holder.title.setText(data.getTitle());
        holder.start.setText(DateConvertUtil.time2string(data.getStartTime()));
        holder.end.setText(DateConvertUtil.time2string(data.getEndTime()));
        holder.address.setText(data.getAddress());
        holder.memo.setText(data.getMemo());
    }

    private void setItemAction(final ViewHolder holder, final ScheduleData data, final TrafficInfoListAdapter adapter) {

        // show more transport information
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isExpanded) {
                    holder.moreInfo.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    holder.moreInfo.setImageResource(R.drawable.ic_action_drop_down);

                    holder.trafficInfoLayout.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    holder.trafficInfoLayout.setVisibility(View.GONE);
                } else {
                    holder.moreInfo.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
                    holder.moreInfo.setImageResource(R.drawable.ic_action_collapse);

                    holder.trafficInfoLayout.setVisibility(View.VISIBLE);
                    holder.trafficInfoLayout.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
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

        // sort traffic info
        holder.sortType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                RealmResults<RouteInfo> routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll().sort("totalDistance");
                switch (position) {
                    case SORT_DIST:
                        routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll().sort("totalDistance");
                        break;

                    case SORT_TIME:
                        routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll().sort("totalTime");
                        break;

                    case SORT_TRANSIT:
                        routeInfos = mRealm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll().sort("totalTransitCount");

                        break;
                }
                adapter.updateData(routeInfos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // set depart time
        holder.setDepartTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int beforeTime = 0;

                switch (position) {

                    case SET_ON_TIME:
                        beforeTime = 0;
                        break;

                    case SET_15_MIN:
                        beforeTime = 15;
                        break;

                    case SET_30_MIN:
                        beforeTime = 30;
                        break;

                    case SET_60_MIN:
                        beforeTime = 60;
                        break;
                }
                EventBus.getDefault().post(new BeforeTimeMessage(data.get_id(), position, beforeTime));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

    private void checkScheduleState(ViewHolder holder, ScheduleData data) {

        // interval = (출발 예상시간) - (현재시간)
        // (출발 예상시간) = (스케줄 시작시간) - (미리 도착시간) - (총 이동소요시간)
        Date expectedDepartTime = DateConvertUtil.calDateMin(data.getStartTime(), data.getBeforeTime()+data.getTotalTime());
        long interval = expectedDepartTime.getTime() - System.currentTimeMillis();
        long result = TimeUnit.MILLISECONDS.toMinutes(interval);
        Log.d("CHECK_INTERVAL", "schedule adapter ----------- " + result);

        if (result < 0) {
            holder.routeInfoLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorShadow));
            holder.departInfo.setText(DateConvertUtil.time2string(expectedDepartTime));
            holder.totalTime.setText(String.format("%d 분", data.getTotalTime()));
        } else if (result >= 0 && result <= 10) {
            holder.routeInfoLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAlert));
            holder.departInfo.setText(String.format("%d 분 안", result));
            holder.totalTime.setText(String.format("%d 분", data.getTotalTime()));
        } else {
            holder.routeInfoLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorDarkNavy));
            holder.departInfo.setText(DateConvertUtil.time2string(expectedDepartTime));
            holder.totalTime.setText(String.format("%d 분", data.getTotalTime()));
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public long getItemId(int index) {
        return super.getItemId(index);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }


}
