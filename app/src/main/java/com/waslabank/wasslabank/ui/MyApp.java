package com.waslabank.wasslabank.ui;

import android.content.Context;

import androidx.multidex.MultiDexApplication;

public class MyApp extends MultiDexApplication {
    private static MyApp instance;



    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }

    public static Context getAppContext() {
        return instance;
    }

    public static MyApp getAppInstance() {
        return instance;
    }
}
