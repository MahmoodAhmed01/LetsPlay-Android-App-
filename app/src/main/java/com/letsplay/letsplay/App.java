package com.letsplay.letsplay;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.zookey.universalpreferences.UniversalPreferences;

/**
 * Created by Mahmood on 6/28/2017.
 */

public class App extends Application {

    static App instance;

    private Gson gson;

    public static String url = "http://192.168.2.106:5000/";


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        UniversalPreferences.initialize(this);
        gson = new Gson();


    }


    public Gson getGson() {
        return gson;
    }

    public static App getInstance() {
        return instance;
    }

    public static KProgressHUD showProgressDialoge(Context context) {
        return KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
    }
}