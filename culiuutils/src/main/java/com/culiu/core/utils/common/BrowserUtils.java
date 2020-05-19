package com.culiu.core.utils.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * @describe 浏览器通用类.
 * @author adison
 * @date: 2014-10-22 下午3:23:40 <br/>
 */
public final class BrowserUtils {

    /**
     * 通过外部浏览器打开页面
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String urlText) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        Uri url = Uri.parse(urlText);
        intent.setData(url);
        context.startActivity(intent);
    }

}
