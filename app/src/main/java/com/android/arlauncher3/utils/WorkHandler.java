package com.android.arlauncher3.utils;

import android.os.Handler;
import android.os.HandlerThread;

public class WorkHandler {
    private HandlerThread handlerThread;
    private Handler handler;

    public WorkHandler(String name) {
        handlerThread = new HandlerThread(name);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public Handler getHandler() {
        if (!handlerThread.isAlive()) {
            throw new IllegalStateException("handlerThread is dead.");
        }
        return handler;
    }


    public void quit() {
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

}
