package com.android.arlauncher3.model;

import android.content.Context;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.SignalStrength;
import android.telephony.TelephonyCallback;
import android.telephony.TelephonyDisplayInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.arlauncher3.utils.WorkHandler;
import com.android.arlauncher3.view.IStatusObserver;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NetworkProvider extends TelephonyCallback implements IStatusProvider,
        TelephonyCallback.SignalStrengthsListener,
        TelephonyCallback.DisplayInfoListener {

    private static final String TAG = "NetworkProvider";
    private Context mContext;
    private TelephonyManager telephonyManager;
    private WorkHandler workHandler;

    private final List<IStatusObserver> observers = new CopyOnWriteArrayList<>();

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
        mContext = context.getApplicationContext();
        telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        workHandler = new WorkHandler("Network-Thread");

        Log.d(TAG, "start: Registering telephony callback on worker thread");
        telephonyManager.registerTelephonyCallback(workHandler.getHandler()::post, this);

        int networkType = telephonyManager.getDataNetworkType();
        Log.d(TAG, "networkType: " + networkType);
        if (!is4GActive(networkType)) {
            notifyObservers(0);
        }
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop: Unregistering and cleaning up");
        if (telephonyManager != null) {
            telephonyManager.unregisterTelephonyCallback(this);
        }
        if (workHandler != null) {
            workHandler.getHandler().removeCallbacksAndMessages(null);
            workHandler.quit();
        }
        observers.clear();
        mContext = null; // 断开引用
    }

    @Override
    public void onDisplayInfoChanged(@NonNull TelephonyDisplayInfo telephonyDisplayInfo) {
        int networkType = telephonyDisplayInfo.getNetworkType();
        Log.d(TAG, "onDisplayInfoChanged: networkType=" + networkType);

        if (!is4GActive(networkType)) {
            notifyObservers(0);
        }
    }

    private boolean is4GActive(int networkType) {
        return (networkType == TelephonyManager.NETWORK_TYPE_LTE
                || networkType == TelephonyManager.NETWORK_TYPE_NR);
    }


    @Override
    public void onSignalStrengthsChanged(@NonNull SignalStrength signalStrength) {
        int newLevel = getSignalLevel(signalStrength);

        notifyObservers(newLevel);
    }

    private void notifyObservers(int level) {
        Log.d(TAG, "notifyObservers: " + level);
        for (IStatusObserver observer : observers) {
            observer.onDataUpdate(StatusType._4G, level);
        }
    }


    private int getSignalLevel(SignalStrength signalStrength) {
        if (signalStrength == null) {
            return 0;
        }
        if (!signalStrength.isGsm() /*&& mConfig.alwaysShowCdmaRssi*/) {
            return getCdmaLevel(signalStrength);
        } else {
            return signalStrength.getLevel();
        }
    }

    private int getCdmaLevel(SignalStrength signalStrength) {
        List<CellSignalStrengthCdma> signalStrengthCdma =
                signalStrength.getCellSignalStrengths(CellSignalStrengthCdma.class);
        if (!signalStrengthCdma.isEmpty()) {
            return signalStrengthCdma.get(0).getLevel();
        }
        return CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN;
    }


}
