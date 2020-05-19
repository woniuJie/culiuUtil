package com.culiu.core.utils.service;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

/**
 * Created by wangjing on 8/11/16.
 */
public class ServiceUtils {

    /**
     * 判断service是否存活
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceAlive(Context context, String serviceName) {
        if (context != null && !TextUtils.isEmpty(serviceName)) {
            ActivityManager activityManager =
                    (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                for (ActivityManager.RunningServiceInfo service :
                        activityManager.getRunningServices(Integer.MAX_VALUE)) {
                    if (service.service == null)
                        continue;
                    if (serviceName.equals(service.service.getClassName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
