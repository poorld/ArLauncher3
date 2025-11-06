package com.android.arlauncher3.view;

import com.android.arlauncher3.model.StatusType;

public interface IStatusObserver {

    void onDataUpdate(StatusType type, Object rawData);
}
