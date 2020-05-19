package com.culiu.core.utils.common;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;

/**
 * Created by wangjing on 2016/1/6.
 */
public class ClipboardUtils {

    public static ClipboardManager getClipboardManager(Context context) {
        return (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
    }

    public static void setText(Context context, String text) {
        if (context != null) {
            ClipboardManager clipboardManager = getClipboardManager(context);
            if (clipboardManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    getClipboardManager(context).setText(text);
                }
            }
        }
    }

    public static String getText(Context context) {
        if (context == null) {
            return "";
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                return getClipboardManager(context).getText().toString();
            } else {
                return "";
            }
        }
    }

}
