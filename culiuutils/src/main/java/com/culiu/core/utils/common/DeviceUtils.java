package com.culiu.core.utils.common;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.culiu.core.utils.debug.DebugLog;
import com.culiu.core.utils.file.FileSize;
import com.culiu.core.utils.file.FileUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author adison
 * @describe 设备信息工具类
 * @date: 2014-10-22 下午3:25:29 <br/>
 */
public class DeviceUtils {

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static String getVersionNameAndVersionCode(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName + "." + info.versionCode;
        } catch (Exception e) {
            /**
             * NameNotFoundException:修改为Exception：： java.lang.RuntimeException: Package manager has died at
             * android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:82) Caused by:
             * android.os.TransactionTooLargeException at android.os.BinderProxy.transact(Binder.java) at
             * android.content.pm.IPackageManager$Stub$Proxy.getPackageInfo(IPackageManager.java:1374) at
             * android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:77)
             **/
            DebugLog.e("getVersionNameAndVersionCode  Exception::", e);
        }
        return "";

    }

    public static String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            /**
             * NameNotFoundException:修改为Exception：： java.lang.RuntimeException: Package manager has died at
             * android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:82) Caused by:
             * android.os.TransactionTooLargeException at android.os.BinderProxy.transact(Binder.java) at
             * android.content.pm.IPackageManager$Stub$Proxy.getPackageInfo(IPackageManager.java:1374) at
             * android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:77)
             **/
            DebugLog.e("getVersionNameAndVersionCode  Exception::", e);
        }
        return "";

    }

    /**
     * 得到手机IMEI
     *
     * @param context
     * @return
     */
    public static String getImei(Context context) {
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String deviceId = tm.getDeviceId();
                if (!TextUtils.isEmpty(deviceId))
                    return deviceId;
            }
        } catch (Exception e) {
            DebugLog.d(e.getMessage());
        }
        // return getMac(context);
        return "";
    }

    /**
     * 得到Mac地址, Android 6.0 无法直接获取到mac地址，需要申请权限让用户授权
     * <p>
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        String mac = null;
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                mac = info.getMacAddress();
                return mac;
            }
        }
        return UUID.randomUUID().toString();
    }

    /**
     * 获取mac地址，兼容6.0
     * 读取wifi的配置文件实现
     *
     * @return
     */
    public static String getMac() {
        String str = "";
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();// 去空格
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (macSerial == null || "".equals(macSerial)) {
            try {
                return FileUtils.loadFileAsString("/sys/class/net/eth0/address")
                        .toUpperCase().substring(0, 17);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return macSerial;
    }

    /**
     * 获取设备唯一标识, 先获取imei，如果取不到，取mac地址，如果在取不到，生成uuid
     *
     * @param context
     * @return
     */
    public static String getDeviceIdentifier(Context context) {
        String imei = getImei(context);
        if (TextUtils.isEmpty(imei)) {
            return getMac(context);
        } else {
            return getImei(context);
        }
    }

    /**
     * 获取androidid
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        if (androidId != null)
            return androidId;
        return UUID.randomUUID().toString();
    }

    /**
     * 获取CPU序列号
     *
     * @return CPU序列号(16位) 读取失败为"0000000000000000"
     */
    public static String getCPUSerial() {
        String str = "", strCPU = "", cpuAddress = "0000000000000000";
        try {
            // 读取CPU信息
            Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            // 查找CPU序列号
            for (int i = 1; i < 100; i++) {
                str = input.readLine();
                if (str != null) {
                    // 查找到序列号所在行
                    if (str.indexOf("Serial") > -1) {
                        // 提取序列号
                        strCPU = str.substring(str.indexOf(":") + 1, str.length());
                        // 去空格
                        cpuAddress = strCPU.trim();
                        break;
                    }
                } else {
                    // 文件结尾
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return cpuAddress;
    }

    /**
     * 获取设备型号
     *
     * @return
     */
    public static String getModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取设备品牌
     *
     * @return
     */
    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取OS版本号
     *
     * @return
     */
    public static String getOSversion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取sdk版本
     *
     * @return
     */
    public static String getSdkVersion() {
        return Build.VERSION.SDK;
    }

    /**
     * 打开拨号界面
     *
     * @param number
     * @param context
     */
    public static void dial(String number, Context context) {
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            TelephonyManager tManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Object iTelephony;
            iTelephony = (Object) getITelephonyMethod.invoke(tManager, (Object[]) null);
            Method dial = iTelephony.getClass().getDeclaredMethod("dial", String.class);
            dial.invoke(iTelephony, number);
        } catch (SecurityException e) {
            DebugLog.i(e.getMessage());
        } catch (NoSuchMethodException e) {
            DebugLog.i(e.getMessage());
        } catch (IllegalArgumentException e) {
            DebugLog.i(e.getMessage());
        } catch (IllegalAccessException e) {
            DebugLog.i(e.getMessage());
        } catch (InvocationTargetException e) {
            DebugLog.i(e.getMessage());
        }
    }

    /**
     * getSystemAvaialbeMemorySize:获得系统可用内存信息. <br/>
     *
     * @param context
     * @return
     * @author adison
     */
    public static String getSystemAvaialbeMemorySize(Context context) {
        // 获得MemoryInfo对象
        MemoryInfo memoryInfo = new MemoryInfo();
        // 获得系统可用内存，保存在MemoryInfo对象上
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        long memSize = memoryInfo.availMem;

        // 字符类型转换

        return FileSize.convertSizeToString(memSize);
    }

    /**
     * getSystemAvaialbeMemorySize:获得系统可用内存信息. <br/>
     *
     * @param context
     * @return
     * @author adison
     */
    public static long getSystemAvaialbeMemory(Context context) {
        // 获得MemoryInfo对象
        MemoryInfo memoryInfo = new MemoryInfo();
        // 获得系统可用内存，保存在MemoryInfo对象上
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);

        return memoryInfo.availMem;
    }

    /**
     * SDK版本大于Android 2.2
     *
     * @return
     */
    public static boolean hasFroyo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * SDK版本大于Android 2.3
     *
     * @return
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    /**
     * SDK版本大于Android3.0
     *
     * @return
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    /**
     * SDK版本大于Android3.1
     *
     * @return
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * SDK版本大于Android4.0
     *
     * @return
     */
    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    /**
     * SDK版本大于Android4.0.3
     *
     * @return
     */
    public static boolean hasIceCreamSandwichMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
    }

    /**
     * SDK版本大于Android4.1
     *
     * @return
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * SDK版本大于Android 4.2
     *
     * @return
     */
    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }

    /**
     * SDK版本大于Android4.4
     *
     * @return
     */
    public static boolean hasKITKAT() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * SDK版本大于Android4.4.4
     *
     * @return
     */
    public static boolean hasKitkatWatch() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH;
    }

    /**
     * SDK版本大于Android 5.0
     *
     * @return
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 客户端标识
     *
     * @return
     */
    public static String getPlatform() {
        return "android";
    }

    /**
     * 获取设备基本信息
     *
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("device brand:").append(getBrand());
        buffer.append(", device os version:").append(getOSversion());
        buffer.append(", device os modle:").append(getModel());
        buffer.append(", imei:").append(getImei(context));
        buffer.append(", app version:").append(getVersionNameAndVersionCode(context));
        return buffer.toString();
    }

    /**
     * 获取imsi
     *
     * @param context
     * @return
     */
    public static String getImsi(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * 获取sdk版本号
     *
     * @return
     */
    public static String getSDKVersion() {
        return Build.VERSION.SDK_INT + "";
    }

    /**
     * 判断DeviceId是否非法
     *
     * @param context
     * @return
     * @author
     */
    public static boolean isDieviceIdUnvalid(Context context) {
        if (TextUtils.isEmpty(getAndroidId(context)) || getAndroidId(context).equals("0"))
            return true;
        return false;
    }

    /**
     * 获取默认的UserAgent
     *
     * @return
     */
    public static String getDefaultUserAgent() {
        return System.getProperty("http.agent");
    }

}
