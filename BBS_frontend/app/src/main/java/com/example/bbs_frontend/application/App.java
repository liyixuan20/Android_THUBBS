package com.example.bbs_frontend.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.example.bbs_frontend.activity.ActivityCollector;
import com.kingja.loadsir.callback.ProgressCallback;
import com.kingja.loadsir.core.LoadSir;

public class App extends Application {
    /**
     * 退出应用程序
     *
     * @param context {Context}
     */
    public static void appExit(Context context) {
        try {
            ActivityCollector.finishAll();
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            assert activityManager != null;
            activityManager.killBackgroundProcesses(context.getPackageName());
            System.exit(0);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ProgressCallback loadingCallback = new ProgressCallback.Builder()
                .setTitle("Loading")
                .build();
        LoadSir.beginBuilder()
                .addCallback(loadingCallback)
                .setDefaultCallback(ProgressCallback.class) //设置默认状态页
                .commit();
    }
}
