package com.culiu.core.utils.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;

import com.culiu.core.utils.debug.DebugLog;

/**
 * 用于快捷方式操作的工具类
 * Created by wangsai on 2016/2/17.
 */
public class ShortcutUtils {

    private ShortcutUtils() {}
    
    /**
     * 判断桌面是否已添加快捷方式，这个对于很多第三方rom不适用，但是如果要覆盖全面rom的话，又会很繁琐，所以
     *
     * @param context
     * @return
     */
    public static boolean hasShortcut(Context context, String title) {
        Cursor c = null;
        try {
            final StringBuilder uriStr = new StringBuilder();
            uriStr.append("content://");
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
                uriStr.append("com.android.launcher.settings");
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                uriStr.append("com.android.launcher2.settings");
            } else {
                uriStr.append("com.android.launcher3.settings");
            }
            uriStr.append("/favorites?notify=true");
            final Uri CONTENT_URI = Uri.parse(uriStr.toString());
            c = context.getContentResolver().query(CONTENT_URI, null, "title=?", new String[]{title}, null);
            if (c != null && c.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            DebugLog.e(e.getMessage());
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    DebugLog.d(e.getMessage());
                }
            }
        }

        return false;
    }

    /**
     * 添加快捷方式到桌面 1.给Intent指定action="com.android.launcher.INSTALL_SHORTCUT"
     * 2.给定义为Intent.EXTRA_SHORTCUT_INENT的Intent设置与安装时一致的action(必须要有) 3.添加权限:com.android.launcher.permission.INSTALL_SHORTCUT
     */
    public static void addShortcutToDesktop(Context context, Class<? extends Activity> activityClass, String title, int resId) {

        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 快捷方式的名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        shortcut.putExtra("duplicate", false); // 不允许重复创建 不一定有作用

        Intent loadIntent = new Intent(Intent.ACTION_MAIN);
        // 指定Activity为快捷方式启动的对象
        loadIntent.setClass(context, activityClass);

        loadIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        loadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        loadIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, loadIntent);
        // 快捷方式的图标
        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context, resId);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        context.sendBroadcast(shortcut);
    }

}
