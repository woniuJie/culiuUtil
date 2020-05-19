package com.culiu.core.utils.io;

/**
 * 对字节进行操作的工具类
 * Created by wangjing on 2015/12/15.
 */
public class ByteUtils {

    /**
     * 转换16进制
     *
     * @param b
     * @return
     */
    public static String getHexString(byte[] b) {
        String info = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            info += hex.toUpperCase();
        }
        return info;
    }

    /**
     * short转换位byte数组
     *
     * @param s
     * @return
     */
    public static byte[] shortToByteArray(short s) {
        byte[] shortBuf = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (shortBuf.length - 1 - i) * 8;
            shortBuf[i] = (byte) ((s >>> offset) & 0xff);
        }
        return shortBuf;
    }

    /**
     * byte数组转换成short
     */
    public static final int byteArrayToShort(byte[] b) {
        return (b[0] << 8) + (b[1] & 0xFF);
    }

    /**
     * 字符串是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        if (s == null || s.length() <= 0) {
            return true;
        }
        return false;
    }


}