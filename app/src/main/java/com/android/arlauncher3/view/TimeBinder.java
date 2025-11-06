package com.android.arlauncher3.view;

import android.widget.TextView;

public class TimeBinder implements IViewBinder{

    private TextView timeView;

    public TimeBinder(TextView timeView) {
        this.timeView = timeView;
    }

    @Override
    public void updateView(ViewStatus status) {
        timeView.setText(status.data);
    }
}
