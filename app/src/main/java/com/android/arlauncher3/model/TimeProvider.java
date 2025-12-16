package com.android.arlauncher3.model;

import android.content.Context;
import android.os.Handler;

import com.android.arlauncher3.utils.WorkHandler;
import com.android.arlauncher3.view.IStatusObserver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class TimeProvider implements IStatusProvider {

    public static final String TAG = "TimeProvider";

    private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private final Runnable timeRunnable = this::updateTime;

    private String mLastTime = "00:00";

    private WorkHandler workHandler;

    private final List<IStatusObserver> observers = new ArrayList<>();

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
        workHandler = new WorkHandler("Time-Thread");
        workHandler.getHandler().post(timeRunnable);
    }

    @Override
    public void stop() {
        workHandler.getHandler().removeCallbacks(timeRunnable);
        workHandler.quit();
    }

    private void updateTime() {
        sdf.setTimeZone(TimeZone.getDefault());
        String time = sdf.format(new Date());
        if (!mLastTime.equals(time)) {
            mLastTime = time;
            notifyTimeUpdate(time);
        }
        workHandler.getHandler().postDelayed(timeRunnable, 5000);
    }

    private void notifyTimeUpdate(String time) {
        for (IStatusObserver observer : observers) {
            observer.onDataUpdate(StatusType.TIME, time);
        }
    }
}
