package com.tedkim.smartschedule.schedule;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    OnTrafficInfoListener mCallback;
    ScheduleRouteListAdapter.ViewHolder mViewHolder;

    public TrafficInfoListAdapter(@Nullable OrderedRealmCollection<RouteInfo> data, boolean autoUpdate, Context context,  ScheduleRouteListAdapter.ViewHolder viewHolder) {
        super(data, autoUpdate);

        mViewHolder = viewHolder;
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnTrafficInfoListener(OnTrafficInfoListener callback){
        this.mCallback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTotalTime, mTransportCount, mPayment;
        LinearLayout mSubPathLayout;
        CardView mSubPathItem;

        public ViewHolder(View itemView) {
            super(itemView);

            mTotalTime = (TextView) itemView.findViewById(R.id.textView_totalTime);
            mTransportCount = (TextView) itemView.findViewById(R.id.textView_transportCount);
            mPayment = (TextView) itemView.findViewById(R.id.textView_payment);
            mSubPathLayout = (LinearLayout) itemView.findViewById(R.id.layout_subPath);
            mSubPathItem = (CardView) itemView.findViewById(R.id.cardView_subPath);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = mInflater.inflate(R.layout.layout_traffic_info_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final RouteInfo data = getItem(position);
        holder.mTotalTime.setText(String.format("%d분", data.getTotalTime()));
        holder.mPayment.setText(String.format("%d원", data.getPayment()));
        holder.mTransportCount.setText(getTransitCount(data));
        holder.mSubPathLayout.removeAllViews();
        getSubPath(holder, data, position);

        holder.mSubPathItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onTrafficInfoClickListener(mViewHolder ,data);
            }
        });

//        holder.mSubPathItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                holder.mSubPathItem.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.white));
//                holder.mSubPathItem.setCardElevation(16.0f);
//            }
//        });
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < data.routeSequence.size(); i++) {

            TextView subPathText = new TextView(mContext);
            subPathText.setGravity(Gravity.CENTER_VERTICAL);

            ImageView subPathIcon = new ImageView(mContext);
            subPathIcon.setLayoutParams(params);

            ImageView subPathNext = new ImageView(mContext);
            subPathNext.setLayoutParams(params);

            RouteSeqData subPath = data.routeSequence.get(i);

            switch (subPath.getTrafficType()) {

                case 1:

                    subPathIcon.setImageResource(R.drawable.ic_subway);
                    holder.mSubPathLayout.addView(subPathIcon);

                    subPathText.setText(subPath.getSubwayName());
                    holder.mSubPathLayout.addView(subPathText);

                    Log.d("SubPath_Check", "path (" + position + ") >>> " + i + " 번째 subPath <<< 지하철 >>> 호선 : " + subPath.getSubwayType() + " / 호선명 : " + subPath.getSubwayName());

                    break;

                case 2:
                    subPathIcon.setImageResource(R.drawable.ic_bus);
                    holder.mSubPathLayout.addView(subPathIcon);

                    subPathText.setText(subPath.getBusName());
                    holder.mSubPathLayout.addView(subPathText);

                    Log.d("SubPath_Check", "path (" + position + ") >>> " + i + " 번째 subPath <<< 버스 >>> 버스타입 : " + subPath.getBusType() + " / 버스명 : " + subPath.getBusName());
                    break;

                case 3:

                    if (i == 0 || i == data.routeSequence.size() - 1) {
                        subPathIcon.setImageResource(R.drawable.ic_walk);
                        holder.mSubPathLayout.addView(subPathIcon);
                        Log.d("SubPath_Check", "path (" + position + ") >>> " + i + " 번째 subPath <<< 도보 >>");
                    } else {
                        continue;
                    }
                    break;
            }

            if (i < data.routeSequence.size() - 1) {

                subPathNext.setImageResource(R.drawable.ic_next_small);
                holder.mSubPathLayout.addView(subPathNext);
            }
        }
        Log.d("SubPath_Check", "---------------------------------------------------------------------");
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }



}
