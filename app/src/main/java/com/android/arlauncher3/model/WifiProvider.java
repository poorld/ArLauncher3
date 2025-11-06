package com.android.arlauncher3.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.android.arlauncher3.utils.WorkHandler;
import com.android.arlauncher3.view.IStatusObserver;

import java.util.ArrayList;
import java.util.List;

public class WifiProvider extends BroadcastReceiver implements IStatusProvider {

    public static final String TAG = "WifiProvider";
    private static final int DELAY_MILLIS = 2000;

    private final List<IStatusObserver> observers = new ArrayList<>();
    private WifiManager mWifiManager;
    private final Runnable wifiSignalRunnable = this::fetchRssi;
    private WorkHandler workHandler;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        int wifi_state = intent.getIntExtra("wifi_state", WifiManager.WIFI_STATE_UNKNOWN);
        switch (wifi_state) {
            case WifiManager.WIFI_STATE_DISABLING:
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                Log.d(TAG, "WIFI_STATE_DISABLED: ");
                workHandler.getHandler().removeCallbacks(wifiSignalRunnable);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                Log.d(TAG, "WIFI_STATE_ENABLED: ");
                workHandler.getHandler().postDelayed(wifiSignalRunnable, DELAY_MILLIS);
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                break;
        }
    }

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
        workHandler = new WorkHandler("Wifi-Thread");
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        IntentFilter wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        context.registerReceiver(this, wifiIntentFilter);
    }

    @Override
    public void stop() {
        mContext.unregisterReceiver(this);
        workHandler.getHandler().removeCallbacks(wifiSignalRunnable);
        workHandler.quit();
        observers.clear();
        mContext = null;
    }

    private void fetchRssi() {
        int rssi = mWifiManager.getConnectionInfo().getRssi();
        notifyObservers(rssi);
        workHandler.getHandler().postDelayed(wifiSignalRunnable, DELAY_MILLIS);
    }

    private void notifyObservers(Object rawData) {
        for (IStatusObserver observer : observers) {
            observer.onDataUpdate(StatusType.WIFI, rawData);
        }
    }
}
