package com.culiu.core.utils.color;

import android.graphics.Color;
import android.text.TextUtils;

/**
 * Created by wangjing on 10/21/16 11:38 AM.
 */
public class ColorUtils {

    private static final String DEFAULT_COLOR = "#FF333333";

    /**
     * @author : zhangyang
     * @date : 2016/1/19
     * @desc :   解析颜色格式 ffffffff
     * @version : 3.0
     */
    public static int parseColor(String colorStr) {
        int color = 0;
        if (TextUtils.isEmpty(colorStr)) return 0;
        try {
            if (!colorStr.startsWith("#")) {
                colorStr = "#".concat(colorStr);
            }
            color = Color.parseColor(colorStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return color;
    }

    /**
     * 解析颜色
     *
     * @param colorString  颜色值，如#FFE0333333，或者ff0000,
     * @param defaultColor 默认颜色
     * @return
     * @author xujianbo
     */
    public static int parseColor(String colorString, String defaultColor) {
        int color = 0;
        try {
            if (TextUtils.isEmpty(colorString)) {
                if (TextUtils.isEmpty(defaultColor))
                    return Color.parseColor(DEFAULT_COLOR);
                else
                    return getColor(defaultColor);
            }
            color = getColor(colorString);

        } catch (Exception e) {
            color = Color.parseColor(DEFAULT_COLOR);
        }
        return color;
    }

    private static int getColor(String color) throws Exception {
        if (TextUtils.isEmpty(color))
            return 0;
        if (!color.startsWith("#")) {
            color = "#".concat(color);
        }
        return Color.parseColor(color);
    }

}
