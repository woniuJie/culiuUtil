package com.culiu.core.utils.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.text.TextUtils;

import com.culiu.core.utils.debug.DebugLog;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author adison
 * @describe APK相关功能通用帮助类.
 * @date: 2014-10-22 下午3:15:26 <br/>
 */
public class ApkUtils {

    /**
     * 安装一个APK文件
     *
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装一个APK文件
     */
    public static void installApk(Context context, Uri data, String type) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(data, type);
        context.startActivity(intent);
    }

    /**
     * 打开某个应用
     *
     * @param context
     * @param packageName
     */
    public static void startApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        // 如果该程序不可启动（像系统自带的包，有很多是没有入口的）会返回NULL
        if (intent != null)
            context.startActivity(intent);
    }

    /**
     * 判断APK包是否已经安装
     *
     * @param context     上下文，一般为Activity
     * @param packageName 包名
     * @return 包存在则返回true，否则返回false
     */
    public static boolean isPackageExists(Context context, String packageName) {
        if (null == packageName || "".equals(packageName)) {
            throw new IllegalArgumentException("Package name cannot be null or empty !");
        }
        try {
            ApplicationInfo info =
                    context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return null != info;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取APK包信息
     *
     * @param context     上下文，一般为Activity
     * @param packageName 包名
     * @return 包存在则返回true，否则返回false
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (null == packageName || "".equals(packageName)) {
            throw new IllegalArgumentException("Package name cannot be null or empty !");
        }
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return info;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 从SD卡文件中获取包名
     *
     * @param context
     * @param filePath 文件路径
     * @return
     */
    public static String getPakcageNameFromFile(Context context, String filePath) {
        PackageInfo info = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
        if (null == info) {
            return null;
        }

        return info.applicationInfo.packageName;
    }

    /**
     * 修改apk文件权限
     *
     * @param permission
     * @param path
     */
    public static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            DebugLog.i(e.getMessage());
        }

    }

    /**
     * 得到应用程序的渠道号
     *
     * @return
     */
    public static String getAppChannel(Context context, String metaDataKey, String defaultValue) {
        String channel = getApplicationMetaData(context, metaDataKey);
        if (TextUtils.isEmpty(channel)) {
            return defaultValue;
        }
        return channel;
    }

    /**
     * 得到Application的指定的KEY的MetaData信息
     *
     * @param metaDataKey
     * @return
     */
    public static String getApplicationMetaData(Context context, String metaDataKey) {
        ApplicationInfo info = null;
        try {
            PackageManager pm = context.getPackageManager();

            info = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);

            return info.metaData.getString(metaDataKey);
        } catch (Exception e) {
            DebugLog.i(e.getMessage());
        }
        return null;
    }

    // /**
    // * 获取渠道号
    // * @param context
    // * @return
    // */
    // public static String getChannel(Context context) {
    // try {
    // ApplicationInfo applicationInfo =
    // context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
    // String string = applicationInfo.metaData.getString("UMENG_CHANNEL");
    // // LogUtil.i(TAG, "UMENG_CHANNEL stringL--->" + string);
    // return string;
    // } catch(NameNotFoundException e) {
    // e.printStackTrace();
    // }
    // return null;
    // }

    /**
     * 获取某个内容里面的meta，比如service或receiver
     *
     * @param context
     * @param metaDataKey
     * @param clazz
     * @return
     */
    public static String getComponentMetaData(Context context, String metaDataKey, Class clazz) {
        try {
            ServiceInfo info = context.getPackageManager()
                    .getServiceInfo(new ComponentName(context, clazz), PackageManager.GET_META_DATA);
            return info.metaData.getString(metaDataKey);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取进程名称
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
     * 获取apk的签名信息
     *
     * @param context
     * @return
     */
    public static String getApkSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature signature = signs[0];
            return encryptionSignture(signature.toByteArray());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * MD5加密
     *
     * @param byteStr 需要加密的内容
     * @return 返回 byteStr的md5值
     */
    private static String encryptionSignture(byte[] byteStr) {
        MessageDigest messageDigest = null;
        StringBuffer md5StrBuff = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(byteStr);
            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                    md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md5StrBuff.toString();
    }

}
