package com.android.arlauncher3.view;

import android.widget.ImageView;

import com.android.arlauncher3.R;

public class BatteryBinder implements IViewBinder {
    private ImageView batteryIcon;

    public BatteryBinder(ImageView imageView) {
        batteryIcon = imageView;
    }


    @Override
    public void updateView(ViewStatus status) {
        switch (status.level) {
            case 0: batteryIcon.setImageResource(R.mipmap.battery_0); break;
            case 1: batteryIcon.setImageResource(R.mipmap.battery_1); break;
            case 2: batteryIcon.setImageResource(R.mipmap.battery_2); break;
            case 3: batteryIcon.setImageResource(R.mipmap.battery_3); break;
            case 4: batteryIcon.setImageResource(R.mipmap.battery_4); break;
        }
    }
}
