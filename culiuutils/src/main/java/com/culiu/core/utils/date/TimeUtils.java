package com.culiu.core.utils.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TimeUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-8-24
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_DATE_NEW = new SimpleDateFormat("yyyy.MM.dd");


    private TimeUtils() {
        throw new AssertionError();
    }

    public static String getPointTime(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE_NEW);
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */


    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    public static String getDetailTime(long timeInMillis) {
        return DEFAULT_DATE_FORMAT.format(new Date(timeInMillis));
    }

    public static String dtFormat(Date date, String dateFormat) {
        return getFormat(dateFormat).format(date);
    }

    private static final DateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * long time to string, format is {@link #DATE_FORMAT_DATE}
     *
     * @param timeInMillis
     * @return
     */
    public static String getSimpleDataTime(long timeInMillis) {
        return getTime(timeInMillis, DATE_FORMAT_DATE);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }


    public static final long ONE_MINUTES = 60;

    public static final long ONE_HOURS = 60 * ONE_MINUTES;

    public static final long ONE_DAY = 24 * ONE_HOURS;

    public static final long TWO_DAY = 2 * ONE_DAY;

    public static final long SEVEN_DAY = 7 * ONE_DAY;


    /**
     * 应用在消息列表界面
     * 时间格式化（单位是毫秒）
     */
    public static String formatTimeStyle2(long time) {
        long currentInSeconds = getCurrentTimeInLong();
        long timeDifference = (currentInSeconds - time) / 1000;
        SimpleDateFormat simpleTimeFormat2 = new SimpleDateFormat("HH:mm");

        String date = DATE_FORMAT_DATE.format(System.currentTimeMillis());

        if (date.equals(DATE_FORMAT_DATE.format(time))) {

            return simpleTimeFormat2.format(time);
        } else {
            if (timeDifference <= TWO_DAY) {
                return "昨天 " + simpleTimeFormat2.format(time);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return format.format(time);
        }
    }

    /**
     * 时间格式化
     *
     * @param time 秒,不是毫秒
     * @return
     */
    public static String formatTime(long time) {
        long timeInMillSeconds = time * 1000;
        long currentInSeconds = getCurrentTimeInLong() / 1000;
        long timeDifference = currentInSeconds - time;
        /**
         * 以秒作单位的话  很容易就得到 0
         */
        if (timeDifference == 0) {
            return "1秒前";
        }
        if (timeDifference > SEVEN_DAY) {   // 具体日期
            return getSimpleDataTime(timeInMillSeconds);
        }
        if (timeDifference <= SEVEN_DAY && timeDifference > TWO_DAY) {   // 几天前
            return (int) Math.floor(timeDifference / ONE_DAY) + "天前";
        }
        if (timeDifference <= TWO_DAY && timeDifference > ONE_DAY) {
            return "昨天";
        }
        if (timeDifference <= ONE_DAY && timeDifference > ONE_HOURS) {
            return (int) Math.floor(timeDifference / ONE_HOURS) + "小时前";
        }
        if (timeDifference <= ONE_HOURS && timeDifference > ONE_MINUTES) {
            return (int) Math.floor(timeDifference / ONE_MINUTES) + "分钟前";
        }
        if (timeDifference <= ONE_MINUTES && timeDifference > 0) {
            return (int) Math.floor(timeDifference) + "秒前";
        }
        return getTime(timeInMillSeconds);
    }

    /**
     * @param millSecondValue 输入的时间毫秒值
     * @return 时间字符串，e.g. "2015.09.25"
     */
    public static String getDateDotString(long millSecondValue) {
        Date date = new Date(millSecondValue);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy'.'MM'.'dd");
        return sdf.format(date);
    }

    /**
     * 获取绝对时间对应以当天为基准的时间值
     *
     * @param secondTime
     */
    public static long getTimeInDay(long secondTime) {
        return secondTime - getDayStart(secondTime);
    }

    /**
     * 获取当天的起始时间
     *
     * @param secondTime
     * @return
     */
    public static long getDayStart(long secondTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(secondTime * 1000);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() / 1000;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy-MM-dd
     */
    public static String getStringDateShort(long l) {
        Date currentTime = new Date(l);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将格式化的时间转换成时间戳
     *
     * @param dateStr
     * @param format
     * @return
     */
    public static long date2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
