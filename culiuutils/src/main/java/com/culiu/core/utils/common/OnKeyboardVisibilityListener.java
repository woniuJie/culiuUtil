package com.culiu.core.utils.common;

/**
 * @ClassName: OnKeyboardVisibilityListener
 * @Description: 软键盘是否可见的回调
 * @author wangjing
 * @date 2015年7月23日 下午7:36:38
 */
public interface OnKeyboardVisibilityListener {

    /**
     * @MethodName: onKeyboardVisibilityChanged
     * @Description: 软键盘可见状态改变
     * @param @param visible
     * @return void
     * @author wangjing
     */
    void onKeyboardVisibilityChanged(boolean visible);
}