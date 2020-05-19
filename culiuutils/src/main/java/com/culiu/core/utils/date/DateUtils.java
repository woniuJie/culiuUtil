package com.culiu.core.utils.date;

import java.util.Calendar;

/**
 * Created by yedr on 2016/5/26.
 */
public class DateUtils {

    private DateUtils() {
    }

    /**
     * getTodayZeroTime:得到今日零时的毫秒数. <br/>
     *
     * @return
     * @author wangheng
     */
    public static long getTodayZeroTime() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.set(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * getTodayHourTime:得到今天指定（小时）的毫秒数. <br/>
     *
     * @param hour
     * @return
     * @author wangheng
     */
    public static long getTodayHourTime(int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);

        calendar.set(Calendar.DAY_OF_YEAR, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * 得到当前的准确时间
     *
     * @return
     */
    public static long getTodayHourSaveTime() {
        Calendar calendar = Calendar.getInstance();
        long time = Calendar.getInstance().getTimeInMillis();
        return time;
    }

    /**
     * 距离当前时间还有多久
     * @param timemillion
     * @return
     */
    public static String getRestTime(long timemillion, long currentSystemTime) {
        long l = timemillion * 1000 - currentSystemTime * 1000;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        // long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        // long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        // System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        return "剩余" + day + "天" + hour + "小时";
    }

    /**
     * 距离当前时间还有多少天
     * @param timemillion
     * @return
     */
    public static String getRestTimeDayOrHour(long timemillion, long currentSystemTime) {
        long l = timemillion * 1000 - currentSystemTime * 1000;
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        if (day > 0 && day < 10) {
            return "剩" + day + "天";
        } else if (day == 0 && hour > 0) {
            return "剩" + hour + "小时";
        } else {
            return "";
        }
    }
}
