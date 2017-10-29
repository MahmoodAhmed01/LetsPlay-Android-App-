package com.letsplay.letsplay;

import android.app.Application;

import com.zookey.universalpreferences.UniversalPreferences;

/**
 * Created by Mahmood on 6/28/2017.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UniversalPreferences.initialize(this);
    }
}
