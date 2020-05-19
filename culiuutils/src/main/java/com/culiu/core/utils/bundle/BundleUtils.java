package com.culiu.core.utils.bundle;

import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by wangjing on 10/21/16 1:56 PM.
 */
public class BundleUtils {

    /**
     * @param @param  bundle
     * @param @param  key
     * @param @return
     *
     * @return String
     *
     * @MethodName: getBundleString
     * @Description: 使用bundle获取字符串的时候, 如果没有这个值,返回空字符串.
     * @author wjing
     */
    public static String getBundleString(Bundle bundle, String key) {
        if (bundle == null)
            return "";
        if (TextUtils.isEmpty(key))
            return "";
        String value = bundle.getString(key);
        return TextUtils.isEmpty(value) ? "" : value;
    }

}
