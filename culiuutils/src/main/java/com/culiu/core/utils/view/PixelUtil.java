package com.culiu.core.utils.view;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.culiu.core.utils.debug.DebugLog;


/**
 * 像素工具
 * 
 * @author wangheng
 * 
 */
public class PixelUtil {
	/**
	 * dip转换成px
	 * 
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue) {
	    try{
    		final float scale = context.getResources()
    				.getDisplayMetrics().density;
    		return (int) (dipValue * scale + 0.5f);
	    }catch(Exception e){
	        DebugLog.e("dip2px occur Exception::", e);
		}
	    return (int)dipValue;
	}

	/**
	 * px 转换成dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources()
				.getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	/**
	 * PX转换成SP
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources()
				.getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}
	/**
	 * SP转换为PX
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);  
    }

	/**
	 * 获得屏幕高度
	 *
	 * @param context
	 *
	 * @return
	 */
	public static int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}
	/**
	 * 获得屏幕高度
	 *
	 * @param context
	 *
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		return getDisplayMetrics(context).widthPixels;
	}

	/**
	 * getDisplayMetrics:返回DisplayMetrics对象，以方便得到屏幕相关信息. <br/>
	 *
	 * @return
	 *
	 * @author wangheng
	 */
	private static DisplayMetrics getDisplayMetrics(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		try {
			WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = manager.getDefaultDisplay();
			if (display != null) {
				display.getMetrics(dm);
			} else {
				dm.setToDefaults();
			}
		} catch (Exception e) {
			DebugLog.e(String.valueOf(e.getMessage()));
		}
		return dm;
	}
}
