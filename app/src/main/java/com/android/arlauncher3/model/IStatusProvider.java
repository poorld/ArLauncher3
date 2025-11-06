package com.android.arlauncher3.model;

import android.content.Context;

import com.android.arlauncher3.view.IStatusObserver;

public interface IStatusProvider {

    void registerObserver(IStatusObserver observer);

    void unRegisterObserver(IStatusObserver observer);

    void start(Context context);

    void stop();
}
