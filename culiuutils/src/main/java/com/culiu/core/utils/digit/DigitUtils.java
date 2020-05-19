package com.culiu.core.utils.digit;

/**
 * Created by yedr on 2016/5/26.
 */
public class DigitUtils {
    public static final float EPSILON_IN_MONEY = 0.001f;

    private DigitUtils() {
    }

    /**
     * 判断浮点型是否相等
     *
     * @param double1
     * @param double2
     * @return
     */
    public static boolean compareDouble(double double1, double double2) {
        if (Math.abs(double1 - double2) < EPSILON_IN_MONEY) {
            return true;
        }
        return false;
    }

    public static boolean compareLong(long long1, long long2) {
        if (Math.abs(long1 - long2) < EPSILON_IN_MONEY) {
            return true;
        }
        return false;
    }
}
