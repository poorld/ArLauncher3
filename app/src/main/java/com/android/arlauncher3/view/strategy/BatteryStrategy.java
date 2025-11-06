package com.android.arlauncher3.view.strategy;

public class BatteryStrategy implements IConvertStrategy{
    @Override
    public int convertToLevel(Object rawData) {
        int battery = Math.abs((Integer) rawData);
        if (battery >= 80) return 4;
        if (battery >= 60) return 3;
        if (battery >= 40) return 2;
        if (battery >= 20) return 1;
        return 0;
    }
}
