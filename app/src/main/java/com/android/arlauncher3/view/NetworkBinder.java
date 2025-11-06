package com.android.arlauncher3.view;

import android.util.Log;
import android.widget.ImageView;

import com.android.arlauncher3.R;

public class NetworkBinder implements IViewBinder {
    private ImageView networkIcon;

    public NetworkBinder(ImageView imageView) {
        networkIcon = imageView;
    }


    @Override
    public void updateView(ViewStatus status) {
        Log.d("NetworkBinder", "updateView: " + status.level);
        switch (status.level) {
            case 0: networkIcon.setImageResource(R.mipmap.net_4g_0); break;
            case 1: networkIcon.setImageResource(R.mipmap.net_4g_1); break;
            case 2: networkIcon.setImageResource(R.mipmap.net_4g_2); break;
            case 3: networkIcon.setImageResource(R.mipmap.net_4g_3); break;
            case 4: networkIcon.setImageResource(R.mipmap.net_4g_4); break;
            case 5: networkIcon.setImageResource(R.mipmap.net_4g_5); break;
        }
    }
}
