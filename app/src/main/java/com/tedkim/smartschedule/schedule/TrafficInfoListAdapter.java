package com.tedkim.smartschedule.schedule;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.tedkim.smartschedule.R;
import com.tedkim.smartschedule.model.RouteInfo;
import com.tedkim.smartschedule.model.RouteSeqData;

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
        TextView mTotalTime, mTransportCount, mPayment;
        LinearLayout mSubPathLayout;

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
            mTotalTime = (TextView) itemView.findViewById(R.id.textView_totalTime);
            mTransportCount = (TextView) itemView.findViewById(R.id.textView_transportCount);
            mPayment = (TextView) itemView.findViewById(R.id.textView_payment);
            mSubPathLayout = (LinearLayout) itemView.findViewById(R.id.layout_subPath);
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
        holder.mTotalTime.setText(String.format("%d분", data.getTotalTime()));
        holder.mPayment.setText(String.format("%d원", data.getPayment()));
        holder.mTransportCount.setText(getTransitCount(data));
        getSubPath(holder, data, position);
    }

    private String getTransitCount(RouteInfo data) {

        String typeCount = "";

        if (data.getBusTransitCount() == 0 && data.getSubwayTransitCount() != 0) {
            typeCount = String.format("지하철 %d", data.getSubwayTransitCount());
        } else if (data.getBusTransitCount() != 0 && data.getSubwayTransitCount() == 0) {
            typeCount = String.format("버스 %d", data.getBusTransitCount());
        } else {
            typeCount = String.format("버스 %d + 지하철 %d", data.getBusTransitCount(), data.getSubwayTransitCount());
        }
        return typeCount;
    }

    private void getSubPath(ViewHolder holder, RouteInfo data, int position) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(0,0,0,0);

        for (int i = 0; i < data.routeSequence.size(); i++) {

            TextView subPathText = new TextView(mContext);
            subPathText.setLayoutParams(params);
            subPathText.setGravity(Gravity.CENTER_VERTICAL);

            ImageView subPathIcon = new ImageView(mContext);
            subPathIcon.setLayoutParams(params);

            ImageView subPathNext = new ImageView(mContext);
            subPathNext.setLayoutParams(params);

            RouteSeqData subPath = data.routeSequence.get(i);

            switch(subPath.getTrafficType()){

                case 1:

                    subPathIcon.setImageResource(R.drawable.ic_subway);
                    holder.mSubPathLayout.addView(subPathIcon);

                    subPathText.setText(subPath.getSubwayName());
                    holder.mSubPathLayout.addView(subPathText);

                    Log.d("SubPath_Check", "path ("+position+") >>> "+i+" 번째 subPath <<< 지하철 >>> 호선 : "+subPath.getSubwayType()+" / 호선명 : "+subPath.getSubwayName());

                    break;

                case 2:
                    subPathIcon.setImageResource(R.drawable.ic_bus);
                    holder.mSubPathLayout.addView(subPathIcon);

                    subPathText.setText(subPath.getBusName());
                    holder.mSubPathLayout.addView(subPathText);

                    Log.d("SubPath_Check", "path ("+position+") >>> "+i+" 번째 subPath <<< 버스 >>> 버스타입 : "+subPath.getBusType()+" / 버스명 : "+subPath.getBusName());
                    break;

                case 3:

                    subPathIcon.setImageResource(R.drawable.ic_walk);
                    holder.mSubPathLayout.addView(subPathIcon);

                    Log.d("SubPath_Check", "path ("+position+") >>> "+i+" 번째 subPath <<< 도보 >>");
                    break;
            }

            if(i<data.routeSequence.size()-1){

                subPathNext.setImageResource(R.drawable.ic_next);
                holder.mSubPathLayout.addView(subPathNext);
            }
        }
        Log.d("SubPath_Check", "---------------------------------------------------------------------");
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_BODY;
        }
    }
}
