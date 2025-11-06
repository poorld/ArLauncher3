package com.android.arlauncher3.view.strategy;

public class NetworkRssiConvertStrategy implements IConvertStrategy{
    @Override
    public int convertToLevel(Object rawData) {
        return Math.abs((Integer) rawData);
    }
}
