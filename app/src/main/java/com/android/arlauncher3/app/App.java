package com.android.arlauncher3.app;

import android.app.Application;
import android.util.Log;

import com.android.arlauncher3.presenter.StatusManager;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "onCreate: ");
        StatusManager.init(getApplicationContext());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("APP", "onTerminate: ");
    }
}
