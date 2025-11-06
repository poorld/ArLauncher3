package com.android.arlauncher3.bean;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.util.Objects;

public class ItemAppEntity {

    private String packageName;
    private String appName;
    private Drawable drawable;

    private boolean isSystemApp;

    private boolean isHookEnabled;

    private boolean selected;
    public ItemAppEntity(String packageName, String appName, Drawable drawable, boolean isSystemApp) {
        this.packageName = packageName;
        this.appName = appName;
        this.drawable = drawable;
        this.isSystemApp = isSystemApp;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public boolean isHookEnabled() {
        return isHookEnabled;
    }

    public void setHookEnabled(boolean hookEnabled) {
        isHookEnabled = hookEnabled;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemAppEntity info = (ItemAppEntity) obj;
        return Objects.equals(packageName, info.getPackageName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(packageName);
    }

    @Override
    public String toString() {
        return "ItemAppEntity{" +
                "packageName='" + packageName + '\'' +
                ", appName='" + appName + '\'' +
                ", drawable=" + drawable +
                ", isSystemApp=" + isSystemApp +
                ", isHookEnabled=" + isHookEnabled +
                '}';
    }
}