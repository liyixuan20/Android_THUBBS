package com.example.bbs_frontend.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {
    private static List<Activity> activities = new ArrayList<>();
    private long exitTime = 0;

    /**
     * 添加 Activity
     */
    static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 移除 Activity
     */
    static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 关闭所有 Activity
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 获取 Activity
     */
    static BaseActivity getActivity(int position) {
        return (BaseActivity) activities.get(position);
    }
}
