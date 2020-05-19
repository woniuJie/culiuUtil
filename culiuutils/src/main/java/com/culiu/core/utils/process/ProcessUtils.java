package com.culiu.core.utils.process;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by wangjing on 10/17/16 3:32 PM.
 */
public class ProcessUtils {

    /**
     * 获取进程名
     *
     * @param context
     * @return
     */
    public static String getProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == activityManager || null == activityManager.getRunningAppProcesses()) {
            return "";
        }

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return "";
    }


    /**
     * 是否是主进程
     *
     * @param context
     * @return
     */
    public static boolean isMainProcess(Context context) {
        if (getPackageName(context).equalsIgnoreCase(getProcessName(context))) {
            return true;
        }
        return false;
    }

    /**
     * 获取包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }


    /**
     * app在前台还是后台
     *
     * @param context
     * @return true 前台, false 后台
     */
    public static boolean isAppRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses =
                activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null)
            return false;
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            if (runningAppProcessInfo.processName.equals(context.getPackageName())) {
                return runningAppProcessInfo.importance ==
                        ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

    /**
     * app 进程是否还存在
     *
     * @param context
     * @return
     */
    public static boolean isAppAlive(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null == activityManager || null == activityManager.getRunningAppProcesses()) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo appProcess
                : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return true;
            }
        }
        return false;
    }

}
