package com.android.arlauncher3.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.arlauncher3.model.BatteryProvider;
import com.android.arlauncher3.model.IStatusProvider;
import com.android.arlauncher3.model.NetworkProvider;
import com.android.arlauncher3.model.StatusType;
import com.android.arlauncher3.model.TimeProvider;
import com.android.arlauncher3.model.WifiProvider;
import com.android.arlauncher3.view.strategy.BatteryStrategy;
import com.android.arlauncher3.view.strategy.IConvertStrategy;
import com.android.arlauncher3.view.IStatusObserver;
import com.android.arlauncher3.view.IViewBinder;
import com.android.arlauncher3.view.strategy.NetworkRssiConvertStrategy;
import com.android.arlauncher3.view.ViewStatus;
import com.android.arlauncher3.view.strategy.WifiRssiConvertStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatusManager implements IStatusObserver {

    private static final String TAG = "StatusManager";
    private static StatusManager INSTANCE;

    private Map<StatusType, IStatusProvider> providers = new HashMap<>();
    private Map<StatusType, IConvertStrategy> strategies = new HashMap<>();
    private Map<StatusType, List<IViewBinder>> viewBinders = new ConcurrentHashMap<>();

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private Context mContext;

    private StatusManager(Context context) {
        mContext = context.getApplicationContext();

        IStatusProvider wifiProvider = new WifiProvider();
        providers.put(StatusType.WIFI, wifiProvider);
        strategies.put(StatusType.WIFI, new WifiRssiConvertStrategy());
        wifiProvider.registerObserver(this);

        IStatusProvider networkProvider = new NetworkProvider();
        providers.put(StatusType._4G, networkProvider);
        strategies.put(StatusType._4G, new NetworkRssiConvertStrategy());
        networkProvider.registerObserver(this);

        IStatusProvider batteryProvider = new BatteryProvider();
        providers.put(StatusType.BATTERY, batteryProvider);
        strategies.put(StatusType.BATTERY, new BatteryStrategy());
        batteryProvider.registerObserver(this);

        IStatusProvider timeProvider = new TimeProvider();
        providers.put(StatusType.TIME, timeProvider);
        timeProvider.registerObserver(this);

    }

    public static void init(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new StatusManager(context);
        }
    }

    public static StatusManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("StatusManager has not been initialized. Call StatusManager.init() in your Application class.");
        }
        return INSTANCE;
    }

    public void registerView(IViewBinder binder, StatusType type) {
        List<IViewBinder> views = viewBinders.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>());
        views.add(binder);
        // Log.d(TAG, "registerView: " + binder);
        // Log.d(TAG, "containsKey: " + viewBinders.containsKey(type));
    }

    public void unregisterView(IViewBinder binder,StatusType type) {
        if (viewBinders.containsKey(type)) {
            List<IViewBinder> views = viewBinders.get(type);
            if (views != null) {
                views.remove(binder);
            }
        }
    }

    public void startAllProviders() {
        for (IStatusProvider provider : providers.values()) {
            provider.start(mContext);
        }
    }

    public void stopAllProviders() {
        for (IStatusProvider provider : providers.values()) {
            provider.stop();
        }
    }


    @Override
    public void onDataUpdate(StatusType type, Object rawData) {
        // Log.d("StatusManager", "Received raw data for " + type);

        ViewStatus viewStatus = new ViewStatus();

        if (strategies.containsKey(type)) {
            IConvertStrategy strategy = strategies.get(type);
            viewStatus.level = strategy.convertToLevel(rawData);
        }

        if (type == StatusType.TIME) {
            viewStatus.data = (String) rawData;
        }

        if (viewBinders.containsKey(type)) {
            for (IViewBinder binder : viewBinders.get(type)) {
                // Log.d(TAG, "updateView: " + binder);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        binder.updateView(viewStatus);

                    }
                });
            }
        }
    }
}
