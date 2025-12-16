package com.android.arlauncher3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.arlauncher3.adapter.ARRecyclerAdapter2;
import com.android.arlauncher3.adapter.ThreeItemsDecoration;
import com.android.arlauncher3.bean.ItemAppEntity;
import com.android.arlauncher3.model.StatusType;
import com.android.arlauncher3.presenter.StatusManager;
import com.android.arlauncher3.utils.PkgManager;
import com.android.arlauncher3.view.BatteryBinder;
import com.android.arlauncher3.view.NetworkBinder;
import com.android.arlauncher3.view.TimeBinder;
import com.android.arlauncher3.view.WifiBinder;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";

    private RecyclerView rv;
    // private ARRecyclerAdapter adapter;
    private ARRecyclerAdapter2 adapter2;
    public static final int DIR_LEFT = 1;
    public static final int DIR_RIGHT = 2;
    private int scrollDirection = -1;
    private LinearLayoutManager layoutManager;

    private Handler mH = new Handler(Looper.getMainLooper());
    private TextView mTvTime;
    private ImageView mIvWifi;
    private ImageView mIvNetwork;
    private ImageView mIvBattery;

    private boolean mInScroll = false;

    private boolean mViewInit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initViews();
        setViewBinder();

    }


    private void initViews() {
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
        rv.scrollToPosition((100 / 2) + 1);

        rv.post(new Runnable() {
            @Override
            public void run() {
                rv.addItemDecoration(new ThreeItemsDecoration(MainActivity.this));

                int centerPosition = findCenterPosition(layoutManager);
                if (centerPosition != RecyclerView.NO_POSITION) {
                    adapter2.setCurrentPosition(centerPosition);
                }
            }
        });

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


        mTvTime = findViewById(R.id.tv_time);
        mIvWifi = findViewById(R.id.iv_wifi);
        mIvNetwork = findViewById(R.id.iv_network);
        mIvBattery = findViewById(R.id.iv_battery);

    }

    private void setViewBinder() {
        StatusManager statusManager = StatusManager.getInstance();
        statusManager.registerView(new TimeBinder(mTvTime), StatusType.TIME);
        statusManager.registerView(new WifiBinder(mIvWifi), StatusType.WIFI);
        statusManager.registerView(new NetworkBinder(mIvNetwork), StatusType._4G);
        statusManager.registerView(new BatteryBinder(mIvBattery), StatusType.BATTERY);

        StatusManager.getInstance().startAllProviders();

    }


    @Override
    protected void onResume() {
        super.onResume();

        // int centerPosition = findCenterPosition(layoutManager);
        // if (centerPosition != RecyclerView.NO_POSITION && centerPosition != adapter2.getCurrentPosition()) {
        //     adapter2.setCurrentPosition(centerPosition);
        // }

        if (!mViewInit) {
            rv.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 手动对齐
                    int centerPosition = findCenterPosition(layoutManager);
                    Log.d(TAG, "findCenterPosition: " + centerPosition);
                    adapter2.setCurrentPosition(adapter2.getCurrentPosition() + 1);
                    rv.smoothScrollToPosition(adapter2.getCurrentPosition() + 1);
                    mViewInit = true;
                }
            }, 100);
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
        // 单点F3 双击F4

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
            adapter2.setCurrentPosition(adapter2.getCurrentPosition() - 1);
            rv.smoothScrollToPosition(adapter2.getCurrentPosition() - 1);
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F6 && event.getAction() == KeyEvent.ACTION_DOWN) {
            // 右滑
            adapter2.setCurrentPosition(adapter2.getCurrentPosition() + 1);
            rv.smoothScrollToPosition(adapter2.getCurrentPosition() + 1);

            // 单点
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_F3 && event.getAction() == KeyEvent.ACTION_DOWN) {
            // ItemAppEntity currentApp = adapter2.getCurrentApp();
            // Log.d(TAG, "startAct " + currentApp.getPackageName());
            // PkgManager.startAct(this, currentApp.getPackageName());
            // mH.postDelayed(new Runnable() {
            //     @Override
            //     public void run() {
            //         rv.scrollToPosition(adapter2.getCurrentPosition() + 1);
            //     }
            // }, 200);
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}