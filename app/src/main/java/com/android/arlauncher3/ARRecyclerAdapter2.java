package com.android.arlauncher3;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ARRecyclerAdapter2 extends RecyclerView.Adapter<ARRecyclerAdapter2.ViewHolder> {

    private static final String TAG = "ArTAG";
    private List<ItemAppEntity> appList;
    private Context mContext;
    private int currentPosition; // 从中间开始，这样可以向两边滚动

    public ARRecyclerAdapter2(Context context) {
        this.mContext = context;
        appList = new ArrayList<>();
    }

    public void setAppList(List<ItemAppEntity> list) {
        appList.clear();
        appList.addAll(list);
        Log.d(TAG, "setAppList: " + appList.size());
        notifyDataSetChanged();
    }


    public void setCurrentPosition(int position) {
        this.currentPosition = position;
        for (int i = 0; i < appList.size(); i++) {
            if (i == getActualPosition(position)) {
                Log.d(TAG, "setCurrentPosition: " + currentPosition);
                Log.d(TAG, "i: " + i);
                appList.get(i).setSelected(true);
            } else {
                appList.get(i).setSelected(false);
            }

        }
        notifyDataSetChanged();
    }

    /*public void setCurrentPosition(int position) {
        int oldPosition = currentPosition;
        this.currentPosition = position;
        int actualOld = getActualPosition(oldPosition);
        int actualNew = getActualPosition(position);
        if (actualOld != actualNew) {
            appList.get(actualOld).setSelected(false);
            appList.get(actualNew).setSelected(true);
            notifyItemChanged(actualOld);
            notifyItemChanged(actualNew);
        }
    }*/

    public int getActualPosition(int position) {
        return position % appList.size();
    }

    public ItemAppEntity getCurrentApp() {
        return appList.get(getActualPosition(currentPosition));
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
        // ItemAppEntity app = appList.get(position);
        ItemAppEntity app = appList.get(getActualPosition(position));
        Log.d(TAG, "onBindViewHolder: " + position);

        // 设置图标和名称
        holder.appIcon.setImageDrawable(app.getDrawable());
        holder.appName.setText(app.getAppName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PkgManager.startAct(mContext, app.getPackageName());
            }
        });

        // 根据位置设置选中状态
        boolean isSelected = position == currentPosition;
        // boolean isSelected = false;
        holder.container.setSelected(isSelected);

        // 根据选中状态设置大小和样式
        if (isSelected) {
            // 当前选中的图标
            // holder.appIcon.setScaleX(1.5f);
            // holder.appIcon.setScaleY(1.5f);
            holder.container.setLayoutParams(new LinearLayout.LayoutParams(130, 130));
            holder.appName.setVisibility(View.VISIBLE);
            holder.itemView.setSelected(true);
        } else {
            // 非选中的图标
            // holder.appIcon.setScaleX(1.0f);
            // holder.appIcon.setScaleY(1.0f);
            holder.appName.setVisibility(View.GONE);
            holder.itemView.setSelected(false);
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
        // return appList.size();
        return 1000;
    }

    public int getCurrentPosition() {
        return currentPosition;
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









