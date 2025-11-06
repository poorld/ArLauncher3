package com.android.arlauncher3.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.util.Log;

import com.android.arlauncher3.utils.WorkHandler;
import com.android.arlauncher3.view.IStatusObserver;

import java.util.ArrayList;
import java.util.List;

public class BatteryProvider implements IStatusProvider {

    private String TAG = "BatteryProvider";

    private static final int UPDATE_INTERVAL = 2000;

    private Context mContext;
    private BatteryManager mBatteryManager;
    private Runnable mUpdateRunnable;

    private int mLastNumber;
    private boolean mLastChargingState;

    private final List<IStatusObserver> observers = new ArrayList<>();

    private WorkHandler workHandler;

    private BroadcastReceiver brReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: " + action);
            if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
                updateChargingStatus(true);
            } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
                updateChargingStatus(false);
            }
        }
    };

    @Override
    public void registerObserver(IStatusObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void unRegisterObserver(IStatusObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void start(Context context) {
        mContext = context;
        mBatteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        workHandler = new WorkHandler("Battery-Thread");

        registerReceivers();
        updateBatteryStatus();

        mUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                updateBatteryStatus();
                workHandler.getHandler().postDelayed(this, UPDATE_INTERVAL);
            }
        };

        workHandler.getHandler().postDelayed(mUpdateRunnable, UPDATE_INTERVAL);
    }

    @Override
    public void stop() {
        mContext.unregisterReceiver(brReceiver);
        workHandler.getHandler().removeCallbacks(mUpdateRunnable);
        workHandler.quit();
        observers.clear();
        mContext = null;
    }

    private void registerReceivers() {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mContext.registerReceiver(brReceiver, filter);
    }


    private void updateBatteryStatus() {
        int batteryPct = getBatteryPercentage();

        boolean isCharging = isCharging();

        int chargingType = getChargingType();
        String chargeTypeStr = getChargeTypeString(chargingType);

        String message = "充电状态: " + (chargingType != 0 ? "正在充电" : "未充电") + "\n" +
                "电量: " + batteryPct + "%\n" +
                "充电类型: " + chargeTypeStr;

        // Log.d(TAG, "updateBatteryStatus: " + message);

        if (mLastChargingState != isCharging) {
            mLastChargingState = isCharging;
            updateChargingStatus(chargingType != 0);
        }

        if (mLastNumber != batteryPct) {
            mLastNumber = batteryPct;
            updateBatteryNumber(batteryPct);
            updateBatteryLevel(batteryPct);
        }

    }


    private void updateBatteryNumber(int number) {
        Log.d(TAG, "updateBatteryNumber: " + number);

    }

    private void updateBatteryLevel(int battery) {
        Log.d(TAG, "updateBatteryLevel: " + battery);
        for (IStatusObserver observer : observers) {
            observer.onDataUpdate(StatusType.BATTERY, battery);
        }
    }

    private void updateChargingStatus(boolean isActive) {
        Log.d(TAG, "updateChargingStatus: " + isActive);

    }

    private int getBatteryPercentage() {
        return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private boolean isCharging() {
        return mBatteryManager.isCharging();
    }

    private int getChargingType() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, iFilter);

        return batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
    }

    private String getChargeTypeString(int plug) {
        switch (plug) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "AC 充电";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB 充电";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "无线充电";
            default:
                return "未充电";
        }
    }


}
