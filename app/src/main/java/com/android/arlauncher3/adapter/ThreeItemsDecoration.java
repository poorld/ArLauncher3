package com.android.arlauncher3.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class ThreeItemsDecoration extends RecyclerView.ItemDecoration {

    private String TAG = "ThreeItemsDecoration";
    private final int spacing; // 条目间距
    private final int sideMargin; // 两侧边距

    public ThreeItemsDecoration(Context context) {
        // 计算间距，假设屏幕宽度显示三个条目
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        spacing = dpToPx(context, 30); // 条目间距 8dp
        sideMargin = (screenWidth - 3 * dpToPx(context, 200) - 2 * spacing) / 2; // 每项200dp，动态计算边距
        Log.d(TAG, "spacing: " + spacing);
        Log.d(TAG, "sideMargin: " + sideMargin);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        // 所有条目添加间距
        outRect.left = spacing / 2;
        outRect.right = spacing / 2;
        // 第一个和最后一个条目添加边距
        if (position == 0) {
            outRect.left = sideMargin;
        }
        if (position == parent.getAdapter().getItemCount() - 1) {
            outRect.right = sideMargin;
        }
    }

    private static int dpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}