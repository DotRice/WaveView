package com.liji.ly.waveview;

import android.app.Application;

import com.socks.library.KLog;

/**
 * Created by ly on 2016/12/14.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KLog.init(true, "liji_use_demo");
    }
}
