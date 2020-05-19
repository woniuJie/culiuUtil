package com.culiu.core.utils.power;

import android.content.Context;
import android.os.PowerManager;

import com.culiu.core.utils.common.DeviceUtils;

/**
 * Created by wangjing on 11/27/16 5:39 PM.
 */
public class PowerUtils {

    /**
     * 判断屏幕是否亮起
     *
     * @param context
     *
     * @return
     */
    public static boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (DeviceUtils.hasKitkatWatch()) {
            return powerManager.isInteractive();
        } else {
            return powerManager.isScreenOn();
        }
    }


    public static void wakeUpScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager
                .newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, PowerUtils.class.getSimpleName());
        wakeLock.acquire();
        wakeLock.release();
    }

    public static void closeScreen(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager
                .newWakeLock(PowerManager.FULL_WAKE_LOCK, PowerUtils.class.getSimpleName());
        wakeLock.release();
    }


}
