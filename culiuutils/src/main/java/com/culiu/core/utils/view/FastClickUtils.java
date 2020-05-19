package com.culiu.core.utils.view;

/**
 * 防止快速点击
 */
public class FastClickUtils {
    private static long lastClickTime;

    public synchronized static boolean isFastClick() {
        return isFastClick(500);
    }

    public synchronized static boolean isFastClick(long interval) {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < interval) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

}