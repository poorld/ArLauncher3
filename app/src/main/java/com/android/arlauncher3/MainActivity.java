package com.android.arlauncher3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    RecyclerView rv;
    // private ARRecyclerAdapter adapter;
    private ARRecyclerAdapter2 adapter2;
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    private int scrollDirection = -1;
    private boolean mInScroll = false;
    private LinearLayoutManager layoutManager;

    private Handler mH = new Handler(Looper.getMainLooper());

    private boolean mViewInit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.rv);

        // adapter = new ARRecyclerAdapter(this);
        adapter2 = new ARRecyclerAdapter2(this);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter2);


        List<ItemAppEntity> systemAndUserApp = PkgManager.getSystemAndUserApp(this);
        adapter2.setAppList(systemAndUserApp);

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv);

        // adapter2.setCurrentPosition(1000/2);
        // 设置循环桌面
        rv.scrollToPosition((1000/2) + 1);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    // 滑动开始时重置 mInScroll
                    mInScroll = false;
                    Log.d(TAG, "onScrollStateChanged: SCROLL_STATE_DRAGGING");
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE && !mInScroll) {
                    int currentPosition = adapter2.getCurrentPosition();
                    int newPosition = scrollDirection == DIR_RIGHT ? currentPosition + 1 : currentPosition - 1;
                    Log.d(TAG, "onScrollStateChanged: currentPosition " + currentPosition);
                    Log.d(TAG, "onScrollStateChanged: newPosition " + newPosition);
                    int centerPosition = findCenterPosition(layoutManager);
                    Log.d(TAG, "onScrollStateChanged: centerPosition " + centerPosition);
                    adapter2.setCurrentPosition(centerPosition);
                    mInScroll = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0) {
                    // 向右滑动
                    scrollDirection = DIR_RIGHT;
                } else if (dx < 0) {
                    // 向左滑动
                    scrollDirection = DIR_LEFT;
                } else {
                    // 无水平滑动
                    scrollDirection = 0;
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!mViewInit) {
            rv.post(new Runnable() {
                @Override
                public void run() {
                    rv.addItemDecoration(new ThreeItemsDecoration(MainActivity.this));
                    int centerPosition = findCenterPosition(layoutManager);
                    Log.d(TAG, "findCenterPosition: " + centerPosition);
                    adapter2.setCurrentPosition(centerPosition);
                    mViewInit = true;

                }
            });
        }
        if (mViewInit) {
            int centerPosition = findCenterPosition(layoutManager);
            int currentPosition = adapter2.getCurrentPosition();
            Log.d(TAG, "onResume: centerPosition " + centerPosition);
            Log.d(TAG, "onResume: currentPosition " + currentPosition);
            if (centerPosition != currentPosition) {
                adapter2.setCurrentPosition(centerPosition);
            }
        }


    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    // Helper method to find the centered item in the RecyclerView
    private int findCenterPosition(LinearLayoutManager layoutManager) {
        int firstVisible = layoutManager.findFirstVisibleItemPosition();
        int lastVisible = layoutManager.findLastVisibleItemPosition();
        if (firstVisible == RecyclerView.NO_POSITION || lastVisible == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        // Find the item closest to the center of the RecyclerView
        int recyclerViewCenter = rv.getWidth() / 2;
        int closestPosition = RecyclerView.NO_POSITION;
        int minDistance = Integer.MAX_VALUE;

        for (int i = firstVisible; i <= lastVisible; i++) {
            View child = layoutManager.findViewByPosition(i);
            if (child != null) {
                int childCenter = (child.getLeft() + child.getRight()) / 2;
                int distance = Math.abs(recyclerViewCenter - childCenter);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestPosition = i;
                }
            }
        }
        return closestPosition;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        long eventTime = event.getEventTime();


        Log.d(TAG, "dispatchKeyEvent: " + event);
        // ok
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            ItemAppEntity currentApp = adapter2.getCurrentApp();
            Log.d(TAG, "startAct " + currentApp.getPackageName());
            PkgManager.startAct(this, currentApp.getPackageName());
            mH.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 确认后会左滑到上一项，在这里先恢复原位
                    // bug待解
                    rv.scrollToPosition(adapter2.getCurrentPosition() + 1);
                }
            }, 200);
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            return true;
            // 左滑
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F5 && event.getAction() == KeyEvent.ACTION_DOWN) {
            adapter2.setCurrentPosition(adapter2.getCurrentPosition() -1);
            rv.smoothScrollToPosition(adapter2.getCurrentPosition() -1);
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F6 && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 右滑
            adapter2.setCurrentPosition(adapter2.getCurrentPosition()  + 1);
            rv.smoothScrollToPosition(adapter2.getCurrentPosition() + 1);

            // 单点
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F7 && event.getAction() == KeyEvent.ACTION_DOWN) {
            ItemAppEntity currentApp = adapter2.getCurrentApp();
            Log.d(TAG, "startAct " + currentApp.getPackageName());
            PkgManager.startAct(this, currentApp.getPackageName());
            mH.postDelayed(new Runnable() {
                @Override
                public void run() {
                    rv.scrollToPosition(adapter2.getCurrentPosition() + 1);
                }
            }, 200);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}