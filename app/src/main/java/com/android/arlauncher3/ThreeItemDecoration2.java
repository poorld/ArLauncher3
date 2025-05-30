package com.android.arlauncher3;


import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ThreeItemDecoration2 extends RecyclerView.ItemDecoration {
    private static final String TAG = "ThreeItemDecoration";
    private final int itemWidth; // 每个item的预期宽度

    public ThreeItemDecoration2(int recyclerViewWidth) {
        Log.d(TAG, "recyclerViewWidth: " + recyclerViewWidth);
        // 假设RecyclerView宽度已知，3个item均分宽度
        this.itemWidth = recyclerViewWidth / 3;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter() != null ? parent.getAdapter().getItemCount() : 0;
        Log.d(TAG, "getItemOffsets: position=" + position);
        Log.d(TAG, "getItemOffsets: itemCount=" + itemCount);
        // 如果item数量少于3，直接返回
        if (itemCount < 3) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        // 计算每个item的宽度和间距
        int parentWidth = parent.getWidth();
        int spacing = (parentWidth - 3 * itemWidth) / 2; // 剩余空间均分到两端
        Log.d(TAG, "getItemOffsets: parentWidth=" + parentWidth);
        Log.d(TAG, "getItemOffsets: spacing=" + spacing);

        // 为第一个和最后一个item设置偏移量
        if (position == 0) {
            // 第一个item，左边添加间距
            outRect.left = spacing;
            outRect.right = 0;
        } else if (position == itemCount - 1 && !isLooperEnabled(parent)) {
            // 最后一个item（非循环模式），右边添加间距
            outRect.left = 0;
            outRect.right = spacing;
        } else {
            // 中间item，无额外间距
            outRect.left = 0;
            outRect.right = 0;
        }

        // 设置顶部和底部间距（可选，保持垂直居中）
        outRect.top = 0;
        outRect.bottom = 0;
    }

    // 检查是否启用了循环模式
    private boolean isLooperEnabled(RecyclerView parent) {
        // RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        // if (layoutManager instanceof LooperLayoutManager) {
        //     return ((LooperLayoutManager) layoutManager).looperEnable;
        // }
        return false;
    }
}