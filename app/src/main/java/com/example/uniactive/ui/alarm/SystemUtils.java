package com.example.uniactive.ui.alarm;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

class SystemUtils {

    public static boolean isAppAlive(Context context, String pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> lists ;
        if (am != null) {
            lists = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : lists) {
                if (appProcess.processName.equals(pid)) {
                    return true;
                }
            }
        }
        return false;
    }
}
