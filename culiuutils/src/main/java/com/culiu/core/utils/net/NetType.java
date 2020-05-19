package com.culiu.core.utils.net;


/**
 * @author adison
 * @describe 网络类型.
 * @date: 2014-10-23 上午10:48:00 <br/>
 */
public enum NetType {

    /**
     * 无网络
     */
    NET_NONE(1),

    /**
     * 手机
     */
    NET_MOBILE(2),

    /**
     * 手机2G
     */
    NET_MOBILE_2G(3),

    /**
     * 手机3G
     */
    NET_MOBILE_3G(4),

    /**
     * 手机4G
     */
    NET_MOBILE_4G(5),

    /**
     * wifi
     */
    NET_WIFI(6),

    /**
     * 其他
     */
    NET_OTHER(7);

    NetType(int value) {
        this.value = value;
    }

    public int value;

}
