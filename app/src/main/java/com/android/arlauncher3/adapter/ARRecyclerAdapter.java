package com.android.arlauncher3.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.arlauncher3.R;
import com.android.arlauncher3.bean.AppBean;

import java.util.ArrayList;
import java.util.List;

public class ARRecyclerAdapter extends RecyclerView.Adapter<ARRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ArTAG";
    private List<AppBean> appList;
    private Context mContext;
    private int currentPosition; // 从中间开始，这样可以向两边滚动

    public ARRecyclerAdapter(Context context) {
        this.mContext = context;
        appList = new ArrayList<>();
    }

    public void setAppList(List<AppBean> list) {
        appList.clear();
        appList.addAll(list);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ar_icon_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 获取实际的应用索引
        AppBean app = appList.get(position);
        Log.d(TAG, "onBindViewHolder: " + position);

        // 设置图标和名称
        holder.appIcon.setImageResource(app.getIcon());
        holder.appName.setText(app.getName());

        // 根据位置设置选中状态
        // boolean isSelected = position == currentPosition;
        boolean isSelected = false;
        holder.container.setSelected(isSelected);

        // 根据选中状态设置大小和样式
        if (isSelected) {
            // 当前选中的图标
            holder.appIcon.setScaleX(1.5f);
            holder.appIcon.setScaleY(1.5f);
            // holder.appName.setVisibility(View.VISIBLE);
        } else {
            // 非选中的图标
            holder.appIcon.setScaleX(1.0f);
            holder.appIcon.setScaleY(1.0f);
            // holder.appName.setVisibility(View.GONE);
        }

        // 设置点击事件
        // holder.itemView.setOnClickListener(v -> {
        //     int oldPosition = currentPosition;
        //     currentPosition = position;
        //     notifyItemChanged(oldPosition);
        //     notifyItemChanged(currentPosition);
        // });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        View container;
        ImageView appIcon;
        TextView appName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.iconContainer);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
        }
    }
}









