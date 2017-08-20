package com.tedkim.smartschedule.schedule;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.util.DateConvertUtil;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by tedkim on 2017. 8. 20..
 */

public class TrafficInfoListAdapter extends RealmRecyclerViewAdapter<RouteInfo, TrafficInfoListAdapter.ViewHolder> {

    Context mContext;
    LayoutInflater mInflater;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_BODY = 1;

    public TrafficInfoListAdapter(@Nullable OrderedRealmCollection<RouteInfo> data, boolean autoUpdate, Context context) {
        super(data, autoUpdate);

        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        // view type
        int mViewType;

        // header view
        Spinner mTrafficType, mSetDepartTime;

        // body view
        TextView mDepartTime, mArriveTime, mTotalTime, mTransportCount, mPayment;
        // TODO - 이동 경로 아이콘으로 보여줄 것

        public ViewHolder(View itemView, int viewType) {
            super(itemView);

            mViewType = viewType;
            if (TYPE_HEADER == mViewType) {
                initHeaderView(itemView);
            } else {
                initBodyView(itemView);
            }
        }

        // TODO - 여기에서 Spinner 동작 정의 할 것
        private void initHeaderView(View itemView) {

            mTrafficType = (Spinner) itemView.findViewById(R.id.spinner_trafficType);
            mSetDepartTime = (Spinner) itemView.findViewById(R.id.spinner_setDepartTime);

        }

        private void initBodyView(View itemView) {

            mDepartTime = (TextView) itemView.findViewById(R.id.textView_departTime);
            mArriveTime = (TextView) itemView.findViewById(R.id.textView_arriveTime);
            mTotalTime = (TextView) itemView.findViewById(R.id.textView_totalTime);
            mTransportCount = (TextView) itemView.findViewById(R.id.textView_transportCount);
            mPayment = (TextView) itemView.findViewById(R.id.textView_payment);
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View headerView = mInflater.inflate(R.layout.layout_traffic_info_header, parent, false);
            return new ViewHolder(headerView, viewType);
        } else {
            View bodyView = mInflater.inflate(R.layout.layout_traffic_info_item, parent, false);
            return new ViewHolder(bodyView, viewType);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder.mViewType == TYPE_HEADER) {
            bindHeaderItem(holder);
        } else {
            bindBodyItem(holder, position - 1);
        }
    }

    // Header 에 바인딩 될 아이템 ??? - 세팅 파라미터 정도 될 듯
    private void bindHeaderItem(ViewHolder holder) {

    }

    private void bindBodyItem(ViewHolder holder, int position) {

        // RealmResults<RouteInfo> routeInfos = realm.where(RouteInfo.class).equalTo("_id", data.get_id()).findAll();

        RouteInfo data = getItem(position);
        holder.mDepartTime.setText(DateConvertUtil.date2string(data.getDepartTime()));
        holder.mArriveTime.setText(DateConvertUtil.date2string(data.getArriveTime()));
        holder.mTotalTime.setText(String.format("%d분", data.getTotalTime()));
        holder.mPayment.setText(String.format("%d원", data.getPayment()));

        String typeCount = "";
        if (data.getBusStationCount() == 0 && data.getSubwayStationCount() != 0) {
            typeCount = String.format("지하철 %d", data.getSubwayStationCount());
        } else if (data.getBusStationCount() != 0 && data.getSubwayStationCount() == 0) {
            typeCount = String.format("버스 %d", data.getBusStationCount());
        } else {
            typeCount = String.format("버스 %d + 지하철 %d", data.getBusStationCount(), data.getSubwayStationCount());
        }
        holder.mTransportCount.setText(typeCount);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount()+1;
    }

    @Override
    public int getItemViewType(int position) {

        if(isPositionHeader(position)){
            return TYPE_HEADER;
        }
        else{
            return TYPE_BODY;
        }
    }
}
