package com.android.arlauncher3.view.strategy;

public class WifiRssiConvertStrategy implements IConvertStrategy{
    @Override
    public int convertToLevel(Object rawData) {
        int rssi = Math.abs((Integer) rawData);
        if (rssi < 65) return 4;
        if (rssi < 75) return 3;
        if (rssi < 85) return 2;
        if (rssi < 100) return 1;
        return 0;
    }
}
