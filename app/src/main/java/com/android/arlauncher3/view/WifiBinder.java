package com.android.arlauncher3.view;

import android.widget.ImageView;

import com.android.arlauncher3.R;

public class WifiBinder implements IViewBinder {
    private ImageView wifiIcon;

    public WifiBinder(ImageView imageView) {
        wifiIcon = imageView;
    }


    @Override
    public void updateView(ViewStatus status) {
        switch (status.level) {
            case 0: wifiIcon.setImageResource(R.mipmap.wifi_0); break;
            case 1: wifiIcon.setImageResource(R.mipmap.wifi_1); break;
            case 2: wifiIcon.setImageResource(R.mipmap.wifi_2); break;
            case 3: wifiIcon.setImageResource(R.mipmap.wifi_3); break;
            case 4: wifiIcon.setImageResource(R.mipmap.wifi_4); break;
        }
    }
}
